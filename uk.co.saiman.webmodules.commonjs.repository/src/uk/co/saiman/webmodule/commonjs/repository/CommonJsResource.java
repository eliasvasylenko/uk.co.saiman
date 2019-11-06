/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.osgi.framework.namespace.IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE;
import static org.osgi.framework.namespace.IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE;
import static org.osgi.framework.namespace.IdentityNamespace.IDENTITY_NAMESPACE;
import static org.osgi.framework.namespace.IdentityNamespace.TYPE_BUNDLE;
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
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.FilterBuilder;
import aQute.bnd.osgi.resource.RequirementBuilder;
import aQute.bnd.version.VersionRange;
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;
import uk.co.saiman.webmodule.WebModuleConstants;
import uk.co.saiman.webmodule.commonjs.Dependency;
import uk.co.saiman.webmodule.commonjs.DependencyKind;
import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;
import uk.co.saiman.webmodule.semver.Range;

public class CommonJsResource implements Resource {
  private final PackageId name;
  private final Version version;
  private final ModuleFormat format;
  private final String entryPoint;

  private final List<CapReqBuilder> requirements;
  private final List<CapReqBuilder> resourceCapabilities;
  private final List<CapReqBuilder> manifestCapabilities;

  CommonJsResource(CommonJsBundleVersion bundleVersion, BundleVersionConfiguration configuration) {
    this.name = bundleVersion.getBundle().getModuleName();
    this.version = bundleVersion.getVersion();
    this.format = configuration.format();
    this.entryPoint = getEntryPoint(bundleVersion.getPackageJson(), configuration);

    /*
     * 
     * 
     * TODO use "browser" to substitute files
     * 
     * 
     */

    this.requirements = concat(
        Stream.of(createExtenderRequirement()),
        createDependencyRequirements(bundleVersion)).collect(toList());
    this.resourceCapabilities = List
        .of(identityCapability(bundleVersion.getBundle().getBundleSymbolicName(format)));
    this.manifestCapabilities = List.of(createModuleServiceCapability());
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

  private CapReqBuilder createModuleServiceCapability() {
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

  private CapReqBuilder identityCapability(String bsn) {
    try {
      CapReqBuilder builder = new CapabilityBuilder(this, IDENTITY_NAMESPACE)
          .setResource(this)
          .addAttribute(IDENTITY_NAMESPACE, bsn)
          .addAttribute(CAPABILITY_VERSION_ATTRIBUTE, version)
          .addAttribute(CAPABILITY_TYPE_ATTRIBUTE, TYPE_BUNDLE);

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
          .addDirective(
              REQUIREMENT_FILTER_DIRECTIVE,
              new FilterBuilder()
                  .and()
                  .eq(EXTENDER_NAMESPACE, EXTENDER_NAME)
                  .literal(versionRange.toFilter())
                  .end()
                  .toString());
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate extender requirement", e);
    }
  }

  private Stream<CapReqBuilder> createDependencyRequirements(CommonJsBundleVersion bundleVersion) {
    try {
      return bundleVersion
          .getDependencies()
          .map(bundleVersion::getDependencyVersion)
          .map(this::createDependencyRequirement)
          .flatMap(Optional::stream);
    } catch (RegistryResolutionException e) {
      throw e;
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate dependency requirements", e);
    }
  }

  private Optional<CapReqBuilder> createDependencyRequirement(Dependency dependency) {
    try {
      if (dependency.getKind() == DependencyKind.VERSION_RANGE) {
        return Optional
            .of(
                new RequirementBuilder(SERVICE_NAMESPACE)
                    .setResource(this)
                    .addAttribute(EXTENDER_VERSION_ATTRIBUTE, EXTENDER_VERSION)
                    .addDirective(
                        REQUIREMENT_FILTER_DIRECTIVE,
                        new FilterBuilder()
                            .and()
                            .eq(CAPABILITY_OBJECTCLASS_ATTRIBUTE, WebModule.class.getName())
                            .eq(ID_ATTRIBUTE, dependency.getPackageId().toString())
                            .literal(
                                dependency
                                    .getVersion(DependencyKind.VERSION_RANGE)
                                    .map(Range::toOsgiFilterString)
                                    .get())
                            .end()
                            .toString()));
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate dependency requirement", e);
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

  public Stream<CapReqBuilder> getResourceCapabilities() {
    return resourceCapabilities.stream();
  }

  public Stream<CapReqBuilder> getManifestCapabilities() {
    return manifestCapabilities.stream();
  }

  public Stream<CapReqBuilder> getRequirements() {
    return requirements.stream();
  }

  @Override
  public List<Capability> getCapabilities(String namespace) {
    return Stream
        .concat(getResourceCapabilities(), getManifestCapabilities())
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
