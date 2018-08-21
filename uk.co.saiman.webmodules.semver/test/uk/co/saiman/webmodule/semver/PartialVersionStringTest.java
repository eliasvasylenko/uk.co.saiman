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

import org.junit.Assert;
import org.junit.Test;

public class PartialVersionStringTest {
  public void parseEmptyString() {
    String string = "";
    PartialVersion version = new PartialVersion(string);

    Assert.assertFalse(version.getMajor().isPresent());
    Assert.assertFalse(version.getMinor().isPresent());
    Assert.assertFalse(version.getMicro().isPresent());

    Assert.assertEquals("*", version.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMajorWithTrailingSpace() {
    new PartialVersion("0 .0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithLeadingSpace() {
    new PartialVersion("0. 0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithTrailingSpace() {
    new PartialVersion("0.0 .0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMicroWithLeadingSpace() {
    new PartialVersion("0.0. 0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMicroWithTrailingSpace() {
    new PartialVersion("0.0.0 -0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMajorWithLetters() {
    new PartialVersion("0a.0.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithLetters() {
    new PartialVersion("0.0a.0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMicroWithLetters() {
    new PartialVersion("0.0.0a");
  }

  @Test
  public void parseAllZeroRelease() {
    String string = "0.0.0";
    PartialVersion version = new PartialVersion(string);

    Assert.assertEquals(0, (int) version.getMajor().get());
    Assert.assertEquals(0, (int) version.getMinor().get());
    Assert.assertEquals(0, (int) version.getMicro().get());
    Assert.assertFalse(version.getPreRelease().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroPreRelease() {
    String string = "0.0.0-0";
    PartialVersion version = new PartialVersion(string);

    Assert.assertEquals(0, (int) version.getMajor().get());
    Assert.assertEquals(0, (int) version.getMinor().get());
    Assert.assertEquals(0, (int) version.getMicro().get());
    Assert.assertEquals(version.getPreRelease().get().toString(), "0");

    Assert.assertEquals(string, version.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseTooManyComponents() {
    new PartialVersion("0.0.0.0");
  }

  @Test
  public void parseWithoutMicro() {
    String string = "0.0";
    PartialVersion version = new PartialVersion(string);

    Assert.assertEquals(0, (int) version.getMajor().get());
    Assert.assertEquals(0, (int) version.getMinor().get());
    Assert.assertFalse(version.getMicro().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseWithoutMinor() {
    String string = "0";
    PartialVersion version = new PartialVersion(string);

    Assert.assertEquals(0, (int) version.getMajor().get());
    Assert.assertFalse(version.getMinor().isPresent());
    Assert.assertFalse(version.getMicro().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseStarMajor() {
    String string = "*";
    PartialVersion version = new PartialVersion(string);

    Assert.assertFalse(version.getMajor().isPresent());
    Assert.assertFalse(version.getMinor().isPresent());
    Assert.assertFalse(version.getMicro().isPresent());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseUppercaseXMajor() {
    String string = "X";
    PartialVersion version = new PartialVersion(string);

    Assert.assertFalse(version.getMajor().isPresent());
    Assert.assertFalse(version.getMinor().isPresent());
    Assert.assertFalse(version.getMicro().isPresent());

    Assert.assertEquals("*", version.toString());
  }

  @Test
  public void parseLowercaseXMajor() {
    String string = "x";
    PartialVersion version = new PartialVersion(string);

    Assert.assertFalse(version.getMajor().isPresent());
    Assert.assertFalse(version.getMinor().isPresent());
    Assert.assertFalse(version.getMicro().isPresent());

    Assert.assertEquals("*", version.toString());
  }

  @Test
  public void parseLargeNumbers() {
    String string = "123.456.789";
    PartialVersion version = new PartialVersion(string);

    Assert.assertEquals(123, (int) version.getMajor().get());
    Assert.assertEquals(456, (int) version.getMinor().get());
    Assert.assertEquals(789, (int) version.getMicro().get());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parsePreReleaseWithExtraDashes() {
    String string = "0.0.0-a-b-c";
    PartialVersion version = new PartialVersion(string);

    Assert.assertEquals(version.getPreRelease().get().toString(), "a-b-c");

    Assert.assertEquals(string, version.toString());
  }
}
