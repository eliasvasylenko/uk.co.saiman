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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VersionStringTest {
  @Test
  public void parseEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse(""));
  }

  @Test
  public void parseMajorWithTrailingSpace() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0 .0.0"));
  }

  @Test
  public void parseMinorWithLeadingSpace() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0. 0.0"));
  }

  @Test
  public void parseMinorWithTrailingSpace() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0 .0"));
  }

  @Test
  public void parseMicroWithLeadingSpace() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0. 0"));
  }

  @Test
  public void parseMicroWithTrailingSpace() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0.0 -0"));
  }

  @Test
  public void parseMajorWithLetters() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0a.0.0"));
  }

  @Test
  public void parseMinorWithLetters() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0a.0"));
  }

  @Test
  public void parseMicroWithLetters() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0.0a"));
  }

  @Test
  public void parseAllZeroRelease() {
    String string = "0.0.0";
    Version version = Version.parse(string);

    assertEquals(0, version.getMajor());
    assertEquals(0, version.getMinor());
    assertEquals(0, version.getMicro());
    assertFalse(version.getPreRelease().isPresent());
    assertFalse(version.getBuildInformation().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroPreRelease() {
    String string = "0.0.0-0";
    Version version = Version.parse(string);

    assertEquals(0, version.getMajor());
    assertEquals(0, version.getMinor());
    assertEquals(0, version.getMicro());
    assertEquals(version.getPreRelease().get().toString(), "0");
    assertFalse(version.getBuildInformation().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroReleaseWithBuildInfo() {
    String string = "0.0.0+0";
    Version version = Version.parse(string);

    assertEquals(0, version.getMajor());
    assertEquals(0, version.getMinor());
    assertEquals(0, version.getMicro());
    assertFalse(version.getPreRelease().isPresent());
    assertEquals(version.getBuildInformation().get(), "0");

    assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroPreReleaseWithBuildInfo() {
    String string = "0.0.0-0+0";
    Version version = Version.parse(string);

    assertEquals(0, version.getMajor());
    assertEquals(0, version.getMinor());
    assertEquals(0, version.getMicro());
    assertEquals(version.getPreRelease().get().toString(), "0");
    assertEquals(version.getBuildInformation().get(), "0");

    assertEquals(string, version.toString());
  }

  @Test
  public void parseTooManyComponents() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0.0.0"));
  }

  @Test
  public void parseTooFewComponents() {
    assertThrows(IllegalArgumentException.class, () -> Version.parse("0.0"));
  }

  @Test
  public void parseLargeNumbers() {
    String string = "123.456.789";
    Version version = Version.parse(string);

    assertEquals(123, version.getMajor());
    assertEquals(456, version.getMinor());
    assertEquals(789, version.getMicro());

    assertEquals(string, version.toString());
  }

  @Test
  public void parsePreReleaseWithExtraDashes() {
    String string = "0.0.0-a-b-c";
    Version version = Version.parse(string);

    assertEquals(version.getPreRelease().get().toString(), "a-b-c");
    assertFalse(version.getBuildInformation().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseBuildInformationWithExtraDashes() {
    String string = "0.0.0+a-b-c";
    Version version = Version.parse(string);

    assertFalse(version.getPreRelease().isPresent());
    assertEquals(version.getBuildInformation().get(), "a-b-c");

    assertEquals(string, version.toString());
  }
}
