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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.osgi.framework.Constants.VERSION_ATTRIBUTE;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.resource.Namespace.REQUIREMENT_FILTER_DIRECTIVE;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_CAPABILITY;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_EXTENDER_NAME;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_EXTENDER_VERSION;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_MAIN_ATTRIBUTE;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_ROOT_ATTRIBUTE;
import static uk.co.saiman.webmodules.commonjs.repository.CommonJsBundleVersion.RESOURCE_ROOT;

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
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

public class CommonJsResource implements Resource {
  private final String name;
  private final Version version;
  private final JSONObject json;

  private List<CapReqBuilder> requirements;
  private List<CapReqBuilder> capabilities;

  CommonJsResource(String name, Version version, JSONObject json) {
    this.name = name;
    this.version = version;
    this.json = json;
  }

  private CapReqBuilder createModuleCapability(JSONObject packageJson) {
    try {
      return new CapabilityBuilder(this, WEB_MODULE_CAPABILITY)
          .setResource(this)
          .addAttribute(WEB_MODULE_MAIN_ATTRIBUTE, packageJson.getString(WEB_MODULE_MAIN_ATTRIBUTE))
          .addAttribute(WEB_MODULE_ROOT_ATTRIBUTE, RESOURCE_ROOT)
          .addAttribute(WEB_MODULE_CAPABILITY, name)
          .addAttribute(VERSION_ATTRIBUTE, version);
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate module capability", e);
    }
  }

  private CapReqBuilder createExtenderRequirement() {
    try {
      aQute.bnd.version.Version version = new aQute.bnd.version.Version(
          WEB_MODULE_EXTENDER_VERSION);
      VersionRange versionRange = new VersionRange(
          version,
          new aQute.bnd.version.Version(version.getMajor() + 1, 0, 0));

      return new RequirementBuilder(EXTENDER_NAMESPACE)
          .setResource(this)
          .addAttribute(EXTENDER_NAMESPACE, WEB_MODULE_EXTENDER_NAME)
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
   * environment and instead rely on the one specified by whomever
   * is wired to fulfill the extender requirement. This saves us
   * from implementing any complex support for minimized jigsaw
   * environments.
   *  
  private CapReqBuilder createExecutionEnvironmentRequirement() {
    return new RequirementBuilder(EXECUTION_ENVIRONMENT_NAMESPACE)
        .setResource(this)
        .addDirective(FILTER_DIRECTIVE, JAVA.J2SE5.getFilter());
  }
   */

  public Stream<CapReqBuilder> getCapabilities() {
    if (capabilities == null) {
      capabilities = Stream.of(createModuleCapability(json)).collect(toList());
    }
    return capabilities.stream();
  }

  public Stream<CapReqBuilder> getRequirements() {
    if (requirements == null) {
      requirements = concat(
          Stream.of(createExtenderRequirement()),
          createDependencyRequirements(json)).collect(toList());
    }
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
