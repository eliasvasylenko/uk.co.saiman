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
 * This file is part of uk.co.saiman.webmodules.commonjs.repository.
 *
 * uk.co.saiman.webmodules.commonjs.repository is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs.repository is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.repository;

import static aQute.bnd.osgi.resource.ResourceUtils.matches;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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
import uk.co.saiman.webmodule.commonjs.Dependency;
import uk.co.saiman.webmodule.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodule.commonjs.registry.Registry;

/**
 * An OSGi repository implementation backed by a CommonJS registry.
 * <p>
 * As NPM is the CommonJS registry provider in widest use support has been
 * provided for certain attempts by NPM to extend the registry to support module
 * formats other than CommonJS.
 * <p>
 * Initial contents are assumed to be configured in the CommonJS module format
 * by default. When resolving the format of the dependencies of a given parent
 * module, we search for the appropriate configuration according to the
 * following precedence:
 * <ol>
 * <li>The configuration in the root which matches the format of the parent and
 * whose version intersects with the version range of the specified
 * dependency.</li>
 * 
 * <li>The first configuration found in the root whose version intersects with
 * the version range of the specified dependency.</li>
 * 
 * <li>Assume a default configuration of CommonJS module format.</li>
 * </ol>
 * 
 * This strategy requires some manual configuration, but it avoids contending
 * with the many conflicting and often under-specified strategies employed
 * within the NPM ecosystem.
 * 
 * @author Elias N Vasylenko
 */
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
  private final RepositoryConfiguration configuration;
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
    this(registry, log, RepositoryConfiguration.loadPath(initialDependencies), cache, bsnPrefix);
  }

  public CommonJsRepository(
      Registry registry,
      Log log,
      RepositoryConfiguration configuration,
      Path cache,
      String bsnPrefix) {
    this.registry = registry;
    this.log = log;
    this.configuration = configuration;
    this.cache = cache;
    this.bsnPrefix = bsnPrefix;

    initialize();
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
      configureInitialBundles();
    } catch (Throwable t) {
      log.log(Level.ERROR, t);
      throw t;
    }
  }

  private void configureInitialBundles() {
    configuration
        .getInitialBundleConfigurations()
        .parallel()
        .forEach(c -> configureBundle(c.getInitialVersionConfigurationRange()));
  }

  void configureBundle(Dependency dependency) {
    CommonJsBundle bundle = resources.get(dependency.getPackageId());

    if (bundle == null) {
      try {
        bundle = fetchBundle(dependency.getPackageId());
      } catch (Exception e) {
        getLog().log(Level.WARN, "Cannot initialize bundles " + configuration, e);
        return;
      }
    }

    bundle.configureDependency(dependency);
  }

  private CommonJsBundle fetchBundle(PackageId id) {
    BundleConfiguration configuration = this.configuration.getBundleConfiguration(id);
    PackageRoot packageRoot = registry.getPackageRoot(id);
    CommonJsBundle bundle = new CommonJsBundle(this, packageRoot, configuration);

    synchronized (resources) {
      if (resources.containsKey(id)) {
        bundle = resources.get(id);
      } else {
        resources.put(id, bundle);
      }
    }

    return bundle;
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
      return version
          .getResources()
          .flatMap(r -> r.getCapabilities(requirement.getNamespace()).stream());
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
