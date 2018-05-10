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

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.osgi.framework.Constants.VERSION_ATTRIBUTE;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.resource.Namespace.REQUIREMENT_FILTER_DIRECTIVE;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_CAPABILITY;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_EXTENDER_NAME;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_EXTENDER_VERSION;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_MAIN_ATTRIBUTE;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_ROOT_ATTRIBUTE;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.RequirementBuilder;
import aQute.bnd.version.VersionRange;
import uk.co.saiman.webmodules.commonjs.registry.Archive;
import uk.co.saiman.webmodules.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodules.commonjs.repository.CommonJsRepository.CommonJsBundle;
import uk.co.saiman.webmodules.semver.Range;

public class CommonJsResource implements Resource {
  private static final int BUFFER_SIZE = 1024;

  private static final String SHASUM_CACHE = "shasum";

  private static final String PACKAGE_SOURCE = "package/";
  private static final String PACKAGE_JSON = "package.json";

  private final CommonJsBundle bundle;
  private final PackageVersion packageVersion;
  private final Version version;

  private final Capability moduleCapability;
  private final Requirement extenderRequirement;
  private final List<Requirement> dependencyRequirements;

  public CommonJsResource(CommonJsBundle bundle, PackageVersion version) {
    this.bundle = bundle;
    this.packageVersion = version;
    this.version = parseSemver(version.getVersion());

    JSONObject packageJson = openPackageJson();

    this.moduleCapability = createModuleCapability(packageJson);
    this.extenderRequirement = createExtenderRequirement();
    this.dependencyRequirements = createDependencyRequirements(packageJson);
  }

  public CommonJsBundle getBundle() {
    return bundle;
  }

  private Path getDistCache() {
    Path cacheRoot = getBundle().getRepository().getCache();
    return hasSha1()
        ? cacheRoot.resolve(SHASUM_CACHE).resolve(getSha1())
        : cacheRoot.resolve(getBundle().getModuleName()).resolve(packageVersion.getVersion());
  }

  private Capability createModuleCapability(JSONObject packageJson) {
    try {
      return new CapabilityBuilder(this, WEB_MODULE_CAPABILITY)
          .setResource(this)
          .addAttribute(WEB_MODULE_MAIN_ATTRIBUTE, packageJson.getString(WEB_MODULE_MAIN_ATTRIBUTE))
          .addAttribute(WEB_MODULE_ROOT_ATTRIBUTE, "static")
          .addAttribute(WEB_MODULE_CAPABILITY, bundle.getModuleName())
          .addAttribute(VERSION_ATTRIBUTE, version)
          .buildCapability();
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate module capability", e);
    }
  }

  private Requirement createExtenderRequirement() {
    try {
      aQute.bnd.version.Version version = new aQute.bnd.version.Version(
          WEB_MODULE_EXTENDER_VERSION);
      VersionRange versionRange = new VersionRange(
          version,
          new aQute.bnd.version.Version(version.getMajor() + 1, 0, 0));

      return new RequirementBuilder(EXTENDER_NAMESPACE)
          .setResource(this)
          .addAttribute(EXTENDER_NAMESPACE, WEB_MODULE_EXTENDER_NAME)
          .addDirective(REQUIREMENT_FILTER_DIRECTIVE, versionRange.toFilter())
          .buildRequirement();
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate extender requirement", e);
    }
  }

  private List<Requirement> createDependencyRequirements(JSONObject packageJson) {
    try {
      // TODO Auto-generated method stub
      return Collections.emptyList();
    } catch (Exception e) {
      throw new RegistryResolutionException("Failed to generate dependency requirements", e);
    }
  }

  private boolean hasSha1() {
    return packageVersion.getSha1().isPresent();
  }

  private String getSha1() {
    return packageVersion.getSha1().map(String::toUpperCase).orElse(null);
  }

  private JSONObject openPackageJson() {
    try {
      return new JSONObject(new JSONTokener(newInputStream(getPackageJson())));
    } catch (JSONException | IOException e) {
      throw new RegistryResolutionException("Failed to open " + PACKAGE_JSON, e);
    }
  }

  private Path getPackageJson() {
    if (hasSha1()) {
      Path cachedPackage = getDistCache().resolve(PACKAGE_JSON);

      if (Files.exists(cachedPackage)) {
        return cachedPackage;
      }
    }

    byte[] bytes;

    if (packageVersion.getArchives().anyMatch(ArchiveType.TARBALL::equals)) {
      bytes = extractTarballPackageJson(packageVersion.getArchive(ArchiveType.TARBALL));

    } else {
      throw new RegistryResolutionException(
          "No supported archive types amongst candidates "
              + packageVersion.getArchives().collect(toList()));
    }

    return writeBytesToCache(PACKAGE_JSON, bytes);
  }

  private byte[] extractTarballPackageJson(Archive archive) {
    try (TarGzInputStream input = new TarGzInputStream(archive.getURL().openStream(), getSha1())) {
      input.findEntry(PACKAGE_SOURCE + PACKAGE_JSON);

      try (ByteArrayOutputStream buffered = new ByteArrayOutputStream()) {
        byte[] readBuffer = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = input.read(readBuffer)) != -1) {
          buffered.write(readBuffer, 0, len);
        }

        return buffered.toByteArray();
      }
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to extract archive from URL " + packageVersion.getUrl(),
          e);
    }
  }

  private Path writeBytesToCache(String fileName, byte[] bytes) {
    try {
      Path destination = getDistCache().resolve(fileName);
      createDirectories(destination.getParent());

      try (BufferedOutputStream buffered = new BufferedOutputStream(newOutputStream(destination))) {
        buffered.write(bytes);
      }

      return destination;
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to write to cache directory " + getDistCache(),
          e);
    }
  }

  @Override
  public List<Capability> getCapabilities(String namespace) {
    return asList(moduleCapability);
  }

  @Override
  public List<Requirement> getRequirements(String namespace) {
    ArrayList<Requirement> requirements = new ArrayList<>(dependencyRequirements.size() + 1);
    requirements.addAll(dependencyRequirements);
    requirements.add(extenderRequirement);
    return Collections.unmodifiableList(requirements);
  }

  protected static Version parseSemver(String versionString) {
    org.osgi.framework.Version osgiVersion = new uk.co.saiman.webmodules.semver.Version(
        versionString).toOsgiVersion();

    return new Version(
        osgiVersion.getMajor(),
        osgiVersion.getMinor(),
        osgiVersion.getMicro(),
        osgiVersion.getQualifier());
  }

  protected static Filter parseSemverRange(String versionRangeString) {
    return new Range(versionRangeString).toOsgiFilter();
  }

  protected Stream<String> getDependencies() {
    // TODO Auto-generated method stub
    return Stream.empty();
  }

  public Version getVersion() {
    return version;
  }
}
