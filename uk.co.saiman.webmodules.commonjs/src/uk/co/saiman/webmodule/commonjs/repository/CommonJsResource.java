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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static org.osgi.resource.Namespace.REQUIREMENT_FILTER_DIRECTIVE;
import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.commonjs.repository.CommonJsBundleVersion.RESOURCE_ROOT;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.ENTRY_POINT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_NAME;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.FORMAT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.RESOURCE_ROOT_ATTRIBUTE;

import java.util.List;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.RequirementBuilder;
import aQute.bnd.version.VersionRange;
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;
import uk.co.saiman.webmodule.WebModuleConstants;
import uk.co.saiman.webmodule.commonjs.registry.RegistryResolutionException;

public class CommonJsResource implements Resource {
  private final PackageId name;
  private final Version version;
  private final ModuleFormat format;
  private final String entryPoint;

  private final List<CapReqBuilder> requirements;
  private final List<CapReqBuilder> capabilities;

  CommonJsResource(
      PackageId name,
      Version version,
      BundleVersionConfiguration configuration,
      JSONObject json) {
    this.name = name;
    this.version = version;
    this.format = configuration.format();
    this.entryPoint = getEntryPoint(json, configuration);

    /*
     * 
     * 
     * TODO use "browser" to substitute files
     * 
     * 
     */

    this.requirements = concat(
        Stream.of(createExtenderRequirement()),
        createDependencyRequirements(json)).collect(toList());
    this.capabilities = Stream.of(createModuleServiceCapability(json)).collect(toList());
  }

  private String getEntryPoint(JSONObject json, BundleVersionConfiguration configuration) {
    return configuration.entryPoint().orElseGet(() -> {
      switch (configuration.format().getId()) {
      case WebModuleConstants.ESM:
        return formatPath(json, "module", "jsnext:main", "es2015");
      default:
        return formatPath(json, "main");
      }
    });
  }

  private String formatPath(JSONObject json, String... attributes) {
    for (String attribute : attributes) {
      String path = json.optString(attribute, null);
      if (path != null) {
        if (path.startsWith("./"))
          path = path.substring(2);

        return path;
      }
    }
    return null;
  }

  private CapReqBuilder createModuleServiceCapability(JSONObject packageJson) {
    try {
      CapReqBuilder builder = new CapabilityBuilder(this, SERVICE_NAMESPACE)
          .setResource(this)
          .addAttribute(CAPABILITY_OBJECTCLASS_ATTRIBUTE, WebModule.class.getName())
          .addAttribute(ID_ATTRIBUTE, name.toString())
          .addAttribute(VERSION_ATTRIBUTE, version)
          .addAttribute(EXTENDER_VERSION_ATTRIBUTE, EXTENDER_VERSION)
          .addAttribute(RESOURCE_ROOT_ATTRIBUTE, RESOURCE_ROOT)
          .addAttribute(ENTRY_POINT_ATTRIBUTE, entryPoint)
          .addAttribute(FORMAT_ATTRIBUTE, format.toString());

      return builder;
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate module capability", e);
    }
  }

  private CapReqBuilder createExtenderRequirement() {
    try {
      aQute.bnd.version.Version version = new aQute.bnd.version.Version(EXTENDER_VERSION);
      VersionRange versionRange = new VersionRange(
          version,
          new aQute.bnd.version.Version(version.getMajor() + 1, 0, 0));

      return new RequirementBuilder(EXTENDER_NAMESPACE)
          .setResource(this)
          .addAttribute(EXTENDER_NAMESPACE, EXTENDER_NAME)
          .addDirective(REQUIREMENT_FILTER_DIRECTIVE, versionRange.toFilter());
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate extender requirement", e);
    }
  }

  private Stream<CapReqBuilder> createDependencyRequirements(JSONObject packageJson) {
    try {
      // TODO Auto-generated method stub
      return Stream.empty();
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate dependency requirements", e);
    }
  }

  /*-
   * For the moment we'll not bother requiring an execution
   * environment. This saves us
   * from implementing any complex support for minimized jigsaw
   * environments.
   *  
  private CapReqBuilder createExecutionEnvironmentRequirement() {
    return new RequirementBuilder(EXECUTION_ENVIRONMENT_NAMESPACE)
        .setResource(this)
        .addDirective(FILTER_DIRECTIVE, JAVA.J2SE5.getFilter());
  }
   */

  public ModuleFormat getFormat() {
    return format;
  }

  public String getEntryPoint() {
    return entryPoint;
  }

  public Stream<CapReqBuilder> getCapabilities() {
    return capabilities.stream();
  }

  public Stream<CapReqBuilder> getRequirements() {
    return requirements.stream();
  }

  @Override
  public List<Capability> getCapabilities(String namespace) {
    return getCapabilities()
        .filter(c -> namespace == null || c.getNamespace().equals(namespace))
        .map(CapReqBuilder::buildCapability)
        .collect(toList());
  }

  @Override
  public List<Requirement> getRequirements(String namespace) {
    return getRequirements()
        .filter(c -> namespace == null || c.getNamespace().equals(namespace))
        .map(CapReqBuilder::buildRequirement)
        .collect(toList());
  }
}
