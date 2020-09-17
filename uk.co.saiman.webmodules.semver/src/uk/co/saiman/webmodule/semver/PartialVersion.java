/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.webmodule.semver;

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.webmodule.semver.Operator.GREATER_THAN;
import static uk.co.saiman.webmodule.semver.Operator.GREATER_THAN_OR_EQUAL;
import static uk.co.saiman.webmodule.semver.Operator.LESS_THAN;
import static uk.co.saiman.webmodule.semver.Operator.LESS_THAN_OR_EQUAL;

import java.util.Optional;

public class PartialVersion {
  private final Integer major;
  private final Integer minor;
  private final Integer micro;
  private final PreReleaseVersion preRelease;

  public PartialVersion() {
    this(null, null, null, null);
  }

  public PartialVersion(int major) {
    this(major, null, null, null);
  }

  public PartialVersion(int major, int minor) {
    this(major, minor, null, null);
  }

  public PartialVersion(int major, int minor, int micro) {
    this(major, minor, micro, null);
  }

  private PartialVersion(
      Integer major,
      Integer minor,
      Integer micro,
      PreReleaseVersion preRelease) {
    this.major = major;
    this.minor = minor;
    this.micro = micro;
    this.preRelease = preRelease;
  }

  public PartialVersion(String versionString) {
    if (versionString.startsWith("="))
      versionString = versionString.substring(1);

    if (versionString.startsWith("v"))
      versionString = versionString.substring(1);

    if (versionString.isEmpty()) {
      versionString = "*";
    }

    String[] components = new String[] { versionString };

    // pre-release
    components = components[0].split("-", 2);
    if (components.length > 1) {
      preRelease = new PreReleaseVersion(components[1]);
    } else {
      preRelease = null;
    }

    // numbers
    components = components[0].split("\\.");
    if (components.length > 3) {
      throw new IllegalArgumentException(
          "invalid version \"" + versionString + "\": invalid format");
    }

    micro = parseInt(components, 2, versionString);
    minor = parseInt(components, 1, versionString);
    major = parseInt(components, 0, versionString);
  }

  public PartialVersion(Version version) {
    this.major = version.getMajor();
    this.minor = version.getMinor();
    this.micro = version.getMicro();
    this.preRelease = version.getPreRelease().orElse(null);
  }

  private Integer parseInt(String[] values, int index, String version) {
    if (values.length <= index
        || values[index].equals("*")
        || values[index].equals("x")
        || values[index].equals("X")) {
      if (!isUnbounded()) {
        throw new IllegalArgumentException(
            "invalid version \"" + version + "\": non-trailing wildcard \"" + values[index] + "\"");
      }
      return null;
    }

    try {
      return Integer.parseInt(values[index]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          "invalid version \"" + version + "\": non-numeric \"" + values[index] + "\"",
          e);
    }
  }

  public boolean isUnbounded() {
    return major == null && minor == null && micro == null && preRelease == null;
  }

  public Optional<Integer> getMajor() {
    return Optional.ofNullable(major);
  }

  public Optional<Integer> getMinor() {
    return Optional.ofNullable(minor);
  }

  public Optional<Integer> getMicro() {
    return Optional.ofNullable(micro);
  }

  public Optional<PreReleaseVersion> getPreRelease() {
    return Optional.ofNullable(preRelease);
  }

  public PartialVersion withPreRelease(String preRelease) {
    return withPreRelease(new PreReleaseVersion(preRelease));
  }

  public PartialVersion withPreRelease(PreReleaseVersion preRelease) {
    return new PartialVersion(major, minor, micro, requireNonNull(preRelease));
  }

  public PartialVersion withoutPreRelease() {
    return new PartialVersion(major, minor, micro, null);
  }

  public PrimitiveComparator getLowerBound() {
    return getBottomBound(false);
  }

  public Optional<PrimitiveComparator> getLowerBoundExclusive() {
    return getTopBound(false);
  }

  public Optional<PrimitiveComparator> getUpperBound() {
    return getTopBound(true);
  }

  public PrimitiveComparator getUpperBoundExclusive() {
    return getBottomBound(true);
  }

  private PrimitiveComparator getBottomBound(boolean upper) {
    Version lowerBound = new Version(
        getMajor().orElse(0),
        getMinor().orElse(0),
        getMicro().orElse(0));

    if (preRelease != null) {
      lowerBound = lowerBound.withPreRelease(preRelease);
    }

    return new PrimitiveComparator(upper ? LESS_THAN : GREATER_THAN_OR_EQUAL, lowerBound);
  }

  private Optional<PrimitiveComparator> getTopBound(boolean upper) {
    if (!getMajor().isPresent()) {
      return Optional.empty();
    }

    if (!getMinor().isPresent()) {
      return Optional
          .of(
              new PrimitiveComparator(
                  upper ? LESS_THAN : GREATER_THAN_OR_EQUAL,
                  new Version(major + 1, 0, 0)));
    }

    if (!getMicro().isPresent()) {
      return Optional
          .of(
              new PrimitiveComparator(
                  upper ? LESS_THAN : GREATER_THAN_OR_EQUAL,
                  new Version(major, minor + 1, 0)));
    }

    Version upperBound = new Version(major, minor, micro);

    if (preRelease != null) {
      upperBound = upperBound.withPreRelease(preRelease);
    }

    return Optional
        .of(new PrimitiveComparator(upper ? LESS_THAN_OR_EQUAL : GREATER_THAN, upperBound));
  }

  @Override
  public String toString() {
    return major == null
        ? "*"
        : major
            + (minor == null
                ? ""
                : "."
                    + minor
                    + (micro == null
                        ? ""
                        : "." + micro + (preRelease == null ? "" : "-" + preRelease)));
  }
}
