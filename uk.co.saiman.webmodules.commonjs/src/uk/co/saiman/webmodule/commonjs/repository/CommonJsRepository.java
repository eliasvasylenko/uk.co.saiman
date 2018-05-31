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
package uk.co.saiman.webmodule.commonjs.repository;

import static aQute.bnd.osgi.resource.ResourceUtils.matches;
import static java.nio.file.Files.newInputStream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONTokener;
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
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodule.commonjs.registry.Registry;
import uk.co.saiman.webmodule.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodule.semver.Range;

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
        name = "Initial Dependencies",
        description = "A path to a JSON file in the format of the value of a \"dependencies\" attribute as specified by CommonJS/NPM")
    String initialDependencies();

    @AttributeDefinition(
        name = "Local Cache",
        description = "A local cache directory for CommonJS artifacts and generated bundles")
    String localCache();

    @AttributeDefinition(
        name = "BSN Prefix",
        description = "The prefix for the BSNs and packages of generated bundles")
    String bsnPrefix();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.webmodules.commonsjs.repository";

  private final Registry registry;
  private final Log log;
  private final Map<PackageId, Range> initialDependencies;
  private final Path cache;
  private final String bsnPrefix;

  private boolean initialized;
  private final Map<PackageId, CommonJsBundle> resources = new HashMap<>();

  /*- TODO
  @Activate
  CommonJsRepository(
      @Reference Registry registry,
      @Reference Log log,
      CommonJsRepositoryConfiguration configuration) {
    this.registry = registry;
    this.log = log;
    this.primaryModules = new HashSet<>(asList(configuration.moduleNames()));
    // TODO load initial dependencies
    configuration.initialDependencies();?
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
      Path initialDependencies,
      Path cache,
      String bsnPrefix) {
    this(registry, log, loadInitialDependencies(initialDependencies), cache, bsnPrefix);
  }

  public CommonJsRepository(
      Registry registry,
      Log log,
      Map<PackageId, Range> initialDependencies,
      Path cache,
      String bsnPrefix) {
    this.registry = registry;
    this.log = log;
    this.initialDependencies = new HashMap<>(initialDependencies);
    this.cache = cache;
    this.bsnPrefix = bsnPrefix;

    initialize();
  }

  private static Map<PackageId, Range> loadInitialDependencies(Path initialDependencies) {
    try (InputStream inputStream = newInputStream(initialDependencies)) {
      JSONObject object = new JSONObject(new JSONTokener(inputStream));

      Map<PackageId, Range> dependencies = new HashMap<>();
      for (String dependency : object.keySet()) {
        Object dependencySpec = object.get(dependency);
        String version;
        if (dependencySpec instanceof String) {
          version = (String) dependencySpec;
        } else if (dependencySpec instanceof JSONObject) {
          version = ((JSONObject) dependencySpec).getString(VERSION_ATTRIBUTE);
        } else {
          throw new IllegalArgumentException("Unrecognised dependency specifier " + dependencySpec);
        }

        dependencies.put(PackageId.parseId(dependency), Range.parse(version));
      }
      return dependencies;
    } catch (Exception e) {
      throw new RegistryResolutionException(
          "Failed to load initial dependencies from path " + initialDependencies,
          e);
    }
  }

  Log getLog() {
    return log;
  }

  public Path getCache() {
    return cache;
  }

  public String getBundleSymbolicNamePrefix() {
    return bsnPrefix;
  }

  private synchronized void initialize() {
    if (!initialized) {
      refresh();
      initialized = true;
    }
  }

  public synchronized void refresh() {
    resources.clear();
    try {
      fetchDependencies(initialDependencies);
    } catch (Throwable t) {
      log.log(Level.ERROR, t);
      throw t;
    }
  }

  private void fetchDependencies(Map<PackageId, Range> dependencies) {
    dependencies
        .keySet()
        .parallelStream()
        .flatMap(this::getOrLoadBundle)
        .flatMap(b -> b.fetchDependencies(dependencies.get(b.getModuleName())))
        .map(v -> v.getDependencies().collect(toMap(identity(), v::getDependencyRange)))
        .forEach(this::fetchDependencies);
  }

  private Stream<CommonJsBundle> getOrLoadBundle(PackageId moduleName) {
    return Optional
        .ofNullable(resources.get(moduleName))
        .map(Stream::of)
        .orElseGet(() -> loadBundle(moduleName));
  }

  private Stream<CommonJsBundle> loadBundle(PackageId moduleName) {
    try {
      PackageRoot packageRoot = registry.getPackageRoot(moduleName);

      synchronized (resources) {
        return Stream
            .of(resources.computeIfAbsent(moduleName, n -> new CommonJsBundle(this, packageRoot)));
      }
    } catch (Exception e) {
      getLog().log(Level.WARN, "Cannot load bundle " + moduleName, e);
      return Stream.empty();
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

  public Set<Capability> findProviders(Requirement requirement) {
    initialize();

    return resources
        .values()
        .stream()
        .flatMap(CommonJsBundle::getBundleVersions)
        .flatMap(version -> getCapabilities(version, requirement))
        .filter(capability -> matches(requirement, capability))
        .collect(toSet());
  }

  private Stream<Capability> getCapabilities(
      CommonJsBundleVersion version,
      Requirement requirement) {
    try {
      return version.getResource().getCapabilities(requirement.getNamespace()).stream();
    } catch (Exception e) {
      getLog()
          .log(
              Level.WARN,
              "Cannot get capabilities "
                  + version.getBundle().getModuleName()
                  + " - "
                  + version.getSemver(),
              e);
      return Stream.empty();
    }
  }

  public Stream<PackageId> getModules() {
    initialize();

    return resources.keySet().stream();
  }

  public Stream<CommonJsBundle> getBundles() {
    initialize();

    return resources.values().stream();
  }

  public Optional<CommonJsBundle> getBundle(PackageId moduleName) {
    initialize();

    return Optional.ofNullable(resources.get(moduleName));
  }
}
