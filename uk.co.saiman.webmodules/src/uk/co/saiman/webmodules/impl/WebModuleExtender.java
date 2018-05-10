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
 * This file is part of uk.co.saiman.webmodules.
 *
 * uk.co.saiman.webmodules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.impl;

import static java.util.Comparator.comparing;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_CAPABILITY;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;

import uk.co.saiman.osgi.ExtenderManager;
import uk.co.saiman.webmodules.WebModule;
import uk.co.saiman.webmodules.WebModules;
import uk.co.saiman.webmodules.WebModulesConstants;

@Capability(
    namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
    name = WebModulesConstants.WEB_MODULE_EXTENDER_NAME,
    version = WebModulesConstants.WEB_MODULE_EXTENDER_VERSION)
@Component(immediate = true)
public class WebModuleExtender extends ExtenderManager implements WebModules {
  public static class ModuleDescriptor {
    public String name;

    public Version version;

    public String root;

    public String main;
  }

  public static class DependencyDescriptor {
    public String name;

    public VersionRange version;
  }

  class WebModuleImpl implements WebModule {
    private final ModuleDescriptor descriptor;
    private final Bundle bundle;

    public WebModuleImpl(BundleCapability capability) {
      Map<String, Object> attributes = new HashMap<>(capability.getAttributes());
      attributes.put("name", attributes.get(WEB_MODULE_CAPABILITY));

      descriptor = converter.convert(attributes).to(ModuleDescriptor.class);
      bundle = capability.getResource().getBundle();
    }

    @Override
    public String name() {
      return descriptor.name;
    }

    @Override
    public Version version() {
      return descriptor.version;
    }

    @Override
    public String main() {
      return descriptor.main;
    }

    @Override
    public URL resource(String name) {
      return bundle.getResource(descriptor.root + "/" + name);
    }

    @Override
    public Stream<WebModule> dependencies() {
      return findRequirements(bundle);
    }
  }

  private final Converter converter = Converters.standardConverter();

  private final Map<Bundle, Set<WebModule>> bundleModules = new HashMap<>();
  private final Map<String, Set<WebModule>> namedModules = new HashMap<>();

  @Override
  protected boolean register(Bundle bundle) {
    BundleWiring wiring = bundle.adapt(BundleWiring.class);
    List<BundleCapability> capabilities = wiring.getCapabilities(WEB_MODULE_CAPABILITY);

    Set<WebModule> modules = new HashSet<>();
    bundleModules.put(bundle, modules);

    for (BundleCapability capability : capabilities) {
      WebModule module = new WebModuleImpl(capability);
      modules.add(module);

      Set<WebModule> modulesOfName = namedModules.get(module.name());
      if (modulesOfName == null) {
        modulesOfName = new HashSet<>();
        namedModules.put(module.name(), modulesOfName);
      }
      modulesOfName.add(module);
    }

    return true;
  }

  Stream<WebModule> findRequirements(Bundle bundle) {
    return bundle
        .adapt(BundleWiring.class)
        .getRequiredWires(WEB_MODULE_CAPABILITY)
        .stream()
        .map(BundleWire::getCapability)
        .map(WebModuleImpl::new);
  }

  @Override
  protected void unregister(Bundle bundle) {
    for (WebModule module : bundleModules.remove(bundle)) {
      Set<WebModule> modulesOfName = namedModules.get(module.name());
      modulesOfName.remove(module);
      if (modulesOfName.isEmpty()) {
        namedModules.remove(module.name());
      }
    }
  }

  @Override
  public synchronized Optional<WebModule> getResource(String moduleName, Version moduleVersion) {
    return namedModules
        .get(moduleName)
        .stream()
        .filter(m -> m.version().equals(moduleVersion))
        .findAny();
  }

  @Override
  public synchronized Stream<WebModule> getResources(
      String moduleName,
      VersionRange moduleVersion) {
    return namedModules.get(moduleName).stream().filter(m -> moduleVersion.includes(m.version()));
  }

  @Override
  public synchronized Stream<Version> getVersions(String moduleName) {
    return namedModules
        .get(moduleName)
        .stream()
        .sorted(comparing(WebModule::version, (a, b) -> b.compareTo(a)))
        .map(WebModule::version);
  }

  @Override
  public synchronized Stream<String> getNames() {
    return namedModules.keySet().stream();
  }
}
