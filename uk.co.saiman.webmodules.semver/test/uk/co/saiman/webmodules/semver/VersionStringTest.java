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

import org.junit.Assert;
import org.junit.Test;

public class VersionStringTest {
  @Test(expected = IllegalArgumentException.class)
  public void parseMajorWithTrailingSpace() {
    new Version("0 .0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithLeadingSpace() {
    new Version("0. 0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithTrailingSpace() {
    new Version("0.0 .0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMicroWithLeadingSpace() {
    new Version("0.0. 0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMicroWithTrailingSpace() {
    new Version("0.0.0 -0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMajorWithLetters() {
    new Version("0a.0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithLetters() {
    new Version("0.0a.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMicroWithLetters() {
    new Version("0.0.0a");
  }

  @Test
  public void parseAllZeroRelease() {
    String string = "0.0.0";
    Version version = new Version(string);

    Assert.assertEquals(0, version.getMajor());
    Assert.assertEquals(0, version.getMinor());
    Assert.assertEquals(0, version.getMicro());
    Assert.assertFalse(version.getPreRelease().isPresent());
    Assert.assertFalse(version.getBuildInformation().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroPreRelease() {
    String string = "0.0.0-0";
    Version version = new Version(string);

    Assert.assertEquals(0, version.getMajor());
    Assert.assertEquals(0, version.getMinor());
    Assert.assertEquals(0, version.getMicro());
    Assert.assertEquals(version.getPreRelease().get().toString(), "0");
    Assert.assertFalse(version.getBuildInformation().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroReleaseWithBuildInfo() {
    String string = "0.0.0+0";
    Version version = new Version(string);

    Assert.assertEquals(0, version.getMajor());
    Assert.assertEquals(0, version.getMinor());
    Assert.assertEquals(0, version.getMicro());
    Assert.assertFalse(version.getPreRelease().isPresent());
    Assert.assertEquals(version.getBuildInformation().get(), "0");

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroPreReleaseWithBuildInfo() {
    String string = "0.0.0-0+0";
    Version version = new Version(string);

    Assert.assertEquals(0, version.getMajor());
    Assert.assertEquals(0, version.getMinor());
    Assert.assertEquals(0, version.getMicro());
    Assert.assertEquals(version.getPreRelease().get().toString(), "0");
    Assert.assertEquals(version.getBuildInformation().get(), "0");

    Assert.assertEquals(string, version.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseTooManyComponents() {
    new Version("0.0.0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseTooFewComponents() {
    new Version("0.0");
  }

  @Test
  public void parseLargeNumbers() {
    String string = "123.456.789";
    Version version = new Version(string);

    Assert.assertEquals(123, version.getMajor());
    Assert.assertEquals(456, version.getMinor());
    Assert.assertEquals(789, version.getMicro());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parsePreReleaseWithExtraDashes() {
    String string = "0.0.0-a-b-c";
    Version version = new Version(string);

    Assert.assertEquals(version.getPreRelease().get().toString(), "a-b-c");
    Assert.assertFalse(version.getBuildInformation().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseBuildInformationWithExtraDashes() {
    String string = "0.0.0+a-b-c";
    Version version = new Version(string);

    Assert.assertFalse(version.getPreRelease().isPresent());
    Assert.assertEquals(version.getBuildInformation().get(), "a-b-c");

    Assert.assertEquals(string, version.toString());
  }
}
