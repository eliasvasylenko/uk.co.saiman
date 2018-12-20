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
package uk.co.saiman.webmodule.semver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class VersionConstructTest {
  @Test
  public void constructAllZeroRelease() {
    Version version = new Version(0, 0, 0);

    assertEquals(version.getMajor(), 0);
    assertEquals(version.getMinor(), 0);
    assertEquals(version.getMicro(), 0);
    assertFalse(version.getPreRelease().isPresent());
    assertFalse(version.getBuildInformation().isPresent());
  }

  @Test
  public void constructAllZeroPreRelease() {
    Version version = new Version(0, 0, 0).withPreRelease("0");

    assertEquals(version.getMajor(), 0);
    assertEquals(version.getMinor(), 0);
    assertEquals(version.getMicro(), 0);
    assertEquals(version.getPreRelease().get().toString(), "0");
    assertFalse(version.getBuildInformation().isPresent());
  }

  @Test
  public void constructAllZeroReleaseWithBuildInfo() {
    Version version = new Version(0, 0, 0).withBuildInformation("0");

    assertEquals(version.getMajor(), 0);
    assertEquals(version.getMinor(), 0);
    assertEquals(version.getMicro(), 0);
    assertFalse(version.getPreRelease().isPresent());
    assertEquals(version.getBuildInformation().get(), "0");
  }

  @Test
  public void constructAllZeroPreReleaseWithBuildInfo() {
    Version version = new Version(0, 0, 0).withPreRelease("0").withBuildInformation("0");

    assertEquals(version.getMajor(), 0);
    assertEquals(version.getMinor(), 0);
    assertEquals(version.getMicro(), 0);
    assertEquals(version.getPreRelease().get().toString(), "0");
    assertEquals(version.getBuildInformation().get(), "0");
  }

  @Test
  public void constructLargeNumbers() {
    Version version = new Version(123, 456, 789);

    assertEquals(version.getMajor(), 123);
    assertEquals(version.getMinor(), 456);
    assertEquals(version.getMicro(), 789);
  }

  @Test
  public void constructPreReleaseWithExtraDashes() {
    Version version = new Version(0, 0, 0).withPreRelease("a-b-c");

    assertEquals(version.getPreRelease().get().toString(), "a-b-c");
    assertFalse(version.getBuildInformation().isPresent());
  }

  @Test
  public void constructBuildInformationWithExtraDashes() {
    Version version = new Version(0, 0, 0).withBuildInformation("a-b-c");

    assertFalse(version.getPreRelease().isPresent());
    assertEquals(version.getBuildInformation().get(), "a-b-c");
  }
}
