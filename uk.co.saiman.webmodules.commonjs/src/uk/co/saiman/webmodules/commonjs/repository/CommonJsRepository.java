/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.webmodules.commonjs.
 *
 * uk.co.saiman.webmodules.commonjs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.commonjs.repository;

import static aQute.bnd.osgi.resource.ResourceUtils.matches;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.repository.Repository;

import aQute.bnd.osgi.repository.BaseRepository;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.webmodules.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.Registry;

@Designate(ocd = CommonJsRepository.CommonJsRepositoryConfiguration.class, factory = true)
@Component(
    immediate = true,
    configurationPid = CommonJsRepository.CONFIGURATION_PID,
    configurationPolicy = ConfigurationPolicy.REQUIRE)
public class CommonJsRepository extends BaseRepository implements Repository {
  @ObjectClassDefinition(
      name = "CommonJS Repository",
      description = "The CommonJS repository service provides OSGi bundles containing javascript module resources, and the capabilities to resolve them")
  public @interface CommonJsRepositoryConfiguration {
    @AttributeDefinition(
        name = "Module Names",
        description = "A list of module names to query and retrieve from the registry")
    String[] moduleNames();

    @AttributeDefinition(
        name = "Local Cache",
        description = "A local cache directory for CommonJS artifacts and generated bundles")
    String localCache();

    @AttributeDefinition(
        name = "BSN Prefix",
        description = "The prefix for the BSNs of generated bundles")
    String bsnPrefix();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.webmodules.commonsjs.repository";

  private final Registry registry;
  private final Log log;
  private final Set<String> primaryModules;
  private final Path cache;
  private final String bsnPrefix;

  private boolean initialized;
  private final Map<String, String> dependencies = new HashMap<>();
  private final Map<String, CommonJsBundle> resources = new HashMap<>();

  /*- TODO
  @Activate
  CommonJsRepository(
      @Reference Registry registry,
      @Reference Log log,
      CommonJsRepositoryConfiguration configuration) {
    this.registry = registry;
    this.log = log;
    this.primaryModules = new HashSet<>(asList(configuration.moduleNames()));
    this.cache = Paths.get(configuration.localCache());
    this.bsnPrefix = configuration.bsnPrefix();
  
    try {
      createDirectories(cache);
    } catch (Exception e) {
      log.log(Level.ERROR, e);
      throw new RegistryResolutionException("Unable to create cache directory " + cache, e);
    }
  }
  */

  public CommonJsRepository(
      Registry registry,
      Log log,
      Collection<? extends String> primaryModules,
      Path cache,
      String bsnPrefix) {
    this.registry = registry;
    this.log = log;
    this.primaryModules = new HashSet<>(primaryModules);
    this.cache = cache;
    this.bsnPrefix = bsnPrefix;

    log.log(Level.WARN, "init... c: " + cache + " b: " + bsnPrefix + " m: " + primaryModules);
    initialize();
    log.log(Level.WARN, "inited!");
  }

  public Path getCache() {
    return cache;
  }

  private synchronized void initialize() {
    if (!initialized) {
      refresh();
      initialized = true;
    }
  }

  public synchronized void refresh() {
    dependencies.clear();
    resources.clear();
    try {
      primaryModules
          .parallelStream()
          .flatMap(this::findPackageRoot)
          .map(this::loadBundle)
          .forEach(CommonJsBundle::fetchAllVersions);
    } catch (Throwable t) {
      log.log(Level.ERROR, t);
      throw t;
    }
  }

  @Override
  public Map<Requirement, Collection<Capability>> findProviders(
      Collection<? extends Requirement> requirements) {
    initialize();

    return requirements
        .stream()
        .distinct()
        .collect(toMap(identity(), this::findProviders, (a, b) -> a, HashMap::new));
  }

  private Stream<PackageRoot> findPackageRoot(String name) {
    try {
      return Stream.of(registry.getPackageRoot(name));
    } catch (Exception e) {
      log.log(Level.WARN, "Cannot locate package root " + name, e);
      return Stream.empty();
    }
  }

  private CommonJsBundle loadBundle(PackageRoot packageRoot) {
    synchronized (resources) {
      return resources.computeIfAbsent(packageRoot.getName(), n -> new CommonJsBundle(packageRoot));
    }
  }

  public Set<Capability> findProviders(Requirement requirement) {
    initialize();

    return resources
        .values()
        .stream()
        .flatMap(CommonJsBundle::getResources)
        .flatMap(resource -> resource.getCapabilities(requirement.getNamespace()).stream())
        .filter(capability -> matches(requirement, capability))
        .collect(toSet());
  }

  public Stream<String> getModules() {
    initialize();

    return resources.keySet().stream();
  }

  public Stream<CommonJsBundle> getBundles() {
    initialize();

    return resources.values().stream();
  }

  public Optional<CommonJsBundle> getBundle(String moduleName) {
    initialize();

    return Optional.ofNullable(resources.get(moduleName));
  }

  public class CommonJsBundle {
    private final PackageRoot packageRoot;
    private final String bundleName;
    private final Map<Version, CommonJsResource> resources;

    public CommonJsBundle(PackageRoot packageRoot) {
      this.bundleName = bsnPrefix + "." + packageRoot.getName();
      this.packageRoot = packageRoot;
      this.resources = new HashMap<>();
    }

    public CommonJsRepository getRepository() {
      return CommonJsRepository.this;
    }

    public String getModuleName() {
      return packageRoot.getName();
    }

    public String getBundleSymbolicName() {
      return bundleName;
    }

    public Stream<Version> getVersions() {
      return resources.keySet().stream();
    }

    public Stream<CommonJsResource> getResources() {
      return resources.values().stream();
    }

    public Optional<CommonJsResource> getResource(Version version) {
      return Optional.ofNullable(resources.get(version));
    }

    private void fetchAllVersions() {
      packageRoot
          .getPackageVersions()
          .parallel()
          .flatMap(v -> findPackageVersion(packageRoot, v))
          .flatMap(v -> loadResource(v))
          .forEach(resource -> {
            add(resource);
            resource
                .getDependencies()
                .parallel()
                .map(registry::getPackageRoot)
                .map(CommonJsRepository.this::loadBundle)
                .forEach(CommonJsBundle::fetchAllVersions);
          });
    }

    private void add(CommonJsResource resource) {
      synchronized (resources) {
        resources.put(resource.getVersion(), resource);
      }
    }

    private Stream<PackageVersion> findPackageVersion(PackageRoot root, String version) {
      try {
        return Stream.of(root.getPackageVersion(version));
      } catch (Exception e) {
        log.log(Level.WARN, "Cannot locate package version " + root.getName() + " - " + version, e);
        return Stream.empty();
      }
    }

    private Stream<CommonJsResource> loadResource(PackageVersion packageVersion) {
      try {
        return Stream.of(new CommonJsResource(this, packageVersion));
      } catch (Exception e) {
        log
            .log(
                Level.WARN,
                "Cannot load resource "
                    + packageVersion.getName()
                    + " - "
                    + packageVersion.getVersion(),
                e);
        return Stream.empty();
      }
    }
  }
}
