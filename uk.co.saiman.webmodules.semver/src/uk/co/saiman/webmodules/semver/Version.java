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
 * This file is part of uk.co.saiman.webmodules.semver.
 *
 * uk.co.saiman.webmodules.semver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.semver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.semver;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

public class Version implements Comparable<Version> {
  public static final String PRE_RELEASE_TAG = "PRE";
  public static final String RELEASE_TAG = "REL";

  private final int major;
  private final int minor;
  private final int micro;
  private final PreReleaseVersion preRelease;
  private final String buildInformation;

  public Version(int major, int minor, int micro) {
    this.major = major;
    this.minor = minor;
    this.micro = micro;
    this.preRelease = null;
    this.buildInformation = null;
  }

  private Version(
      int major,
      int minor,
      int micro,
      PreReleaseVersion preRelease,
      String buildInformation) {
    this.major = major;
    this.minor = minor;
    this.micro = micro;
    this.preRelease = preRelease;
    this.buildInformation = buildInformation;
  }

  public static Version parse(String versionString) {
    int major;
    int minor;
    int micro;
    PreReleaseVersion preRelease;
    String buildInformation;

    if (versionString.startsWith("="))
      versionString = versionString.substring(1);

    if (versionString.startsWith("v"))
      versionString = versionString.substring(1);

    String[] components = new String[] { versionString };

    // build information
    components = components[0].split("\\+", 2);
    if (components.length > 1) {
      buildInformation = components[1];
    } else {
      buildInformation = null;
    }

    // pre-release
    components = components[0].split("-", 2);
    if (components.length > 1) {
      preRelease = new PreReleaseVersion(components[1]);
    } else {
      preRelease = null;
    }

    // numbers
    components = components[0].split("\\.");
    if (components.length != 3) {
      throw new IllegalArgumentException(
          "invalid version \"" + versionString + "\": invalid format");
    }

    major = parseInt(components[0], versionString);
    minor = parseInt(components[1], versionString);
    micro = parseInt(components[2], versionString);

    Version version = new Version(major, minor, micro);
    if (preRelease != null)
      version = version.withPreRelease(preRelease);
    if (buildInformation != null)
      version = version.withBuildInformation(buildInformation);
    return version;
  }

  private static int parseInt(String value, String version) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "invalid version \"" + version + "\": non-numeric \"" + value + "\"",
          e);
    }
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor;
  }

  public int getMicro() {
    return micro;
  }

  public Optional<PreReleaseVersion> getPreRelease() {
    return Optional.ofNullable(preRelease);
  }

  public Version withPreRelease(String preRelease) {
    return withPreRelease(new PreReleaseVersion(preRelease));
  }

  public Version withPreRelease(PreReleaseVersion preRelease) {
    return new Version(major, minor, micro, requireNonNull(preRelease), buildInformation);
  }

  public Version withoutPreRelease() {
    return new Version(major, minor, micro, null, buildInformation);
  }

  public Optional<String> getBuildInformation() {
    return Optional.ofNullable(buildInformation);
  }

  public Version withBuildInformation(String buildInformation) {
    if (buildInformation.matches(".*\\s+.*"))
      throw new IllegalArgumentException(
          "Invalid build information: \"" + buildInformation + "\" contains whitespace");

    return new Version(major, minor, micro, preRelease, requireNonNull(buildInformation));
  }

  public Version withoutBuildInformation() {
    return new Version(major, minor, micro, preRelease, null);
  }

  public Version withMajorBump() {
    return new Version(major + 1, 0, 0);
  }

  public Version withMinorBump() {
    return new Version(major, minor + 1, 0);
  }

  public Version withMicroBump() {
    return new Version(major, minor, micro + 1);
  }

  public boolean isRelease() {
    return preRelease == null;
  }

  public org.osgi.framework.Version toOsgiVersion() {
    return new org.osgi.framework.Version(
        major,
        minor,
        micro,
        isRelease() ? RELEASE_TAG : (PRE_RELEASE_TAG + "-" + preRelease.toOsgiQualifier()));
  }

  public static Version fromOsgiVersion(org.osgi.framework.Version osgiVersion) {
    Version version = new Version(
        osgiVersion.getMajor(),
        osgiVersion.getMinor(),
        osgiVersion.getMicro());

    String qualifier = osgiVersion.getQualifier();

    if (qualifier.startsWith(PRE_RELEASE_TAG + "-")) {
      qualifier = qualifier.substring((PRE_RELEASE_TAG + "-").length());
      version = version.withPreRelease(qualifier);

    } else if (!qualifier.equals(RELEASE_TAG)) {
      throw new IllegalArgumentException(
          "invalid version \"" + version + "\": unrecognised qualifier \"" + qualifier + "\"");
    }

    return version;
  }

  @Override
  public String toString() {
    return major
        + "."
        + minor
        + "."
        + micro
        + (preRelease != null ? "-" + preRelease : "")
        + (buildInformation != null ? "+" + buildInformation : "");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof Version))
      return false;

    Version that = (Version) obj;

    return this.major == that.major
        && this.minor == that.minor
        && this.micro == that.micro
        && Objects.equals(this.preRelease, that.preRelease);
  }

  @Override
  public int hashCode() {
    return Objects.hash(major, minor, micro, preRelease);
  }

  @Override
  public int compareTo(Version that) {
    int comparison = (this.major != that.major)
        ? Integer.compare(this.major, that.major)

        : (this.minor != that.minor)
            ? Integer.compare(this.minor, that.minor)

            : Integer.compare(this.micro, that.micro);

    if (comparison == 0) {
      comparison = this.isRelease()
          ? (that.isRelease() ? 0 : 1)
          : (that.isRelease() ? -1 : this.preRelease.compareTo(that.preRelease));
    }

    return comparison;
  }
}
