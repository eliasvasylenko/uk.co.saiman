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

public class PartialVersionStringTest {
  public void parseEmptyString() {
    String string = "";
    PartialVersion version = new PartialVersion(string);

    assertFalse(version.getMajor().isPresent());
    assertFalse(version.getMinor().isPresent());
    assertFalse(version.getMicro().isPresent());

    assertEquals("*", version.toString());
  }

  @Test
  public void parseMajorWithTrailingSpace() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0 .0.0"));
  }

  @Test
  public void parseMinorWithLeadingSpace() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0. 0.0"));
  }

  @Test
  public void parseMinorWithTrailingSpace() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0.0 .0"));
  }

  @Test
  public void parseMicroWithLeadingSpace() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0.0. 0"));
  }

  @Test
  public void parseMicroWithTrailingSpace() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0.0.0 -0"));
  }

  @Test
  public void parseMajorWithLetters() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0a.0.0"));
  }

  @Test
  public void parseMinorWithLetters() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0.0a.0"));
  }

  @Test
  public void parseMicroWithLetters() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0.0.0a"));
  }

  @Test
  public void parseAllZeroRelease() {
    String string = "0.0.0";
    PartialVersion version = new PartialVersion(string);

    assertEquals(0, (int) version.getMajor().get());
    assertEquals(0, (int) version.getMinor().get());
    assertEquals(0, (int) version.getMicro().get());
    assertFalse(version.getPreRelease().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseAllZeroPreRelease() {
    String string = "0.0.0-0";
    PartialVersion version = new PartialVersion(string);

    assertEquals(0, (int) version.getMajor().get());
    assertEquals(0, (int) version.getMinor().get());
    assertEquals(0, (int) version.getMicro().get());
    assertEquals(version.getPreRelease().get().toString(), "0");

    assertEquals(string, version.toString());
  }

  @Test
  public void parseTooManyComponents() {
    assertThrows(IllegalArgumentException.class, () -> new PartialVersion("0.0.0.0"));
  }

  @Test
  public void parseWithoutMicro() {
    String string = "0.0";
    PartialVersion version = new PartialVersion(string);

    assertEquals(0, (int) version.getMajor().get());
    assertEquals(0, (int) version.getMinor().get());
    assertFalse(version.getMicro().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseWithoutMinor() {
    String string = "0";
    PartialVersion version = new PartialVersion(string);

    assertEquals(0, (int) version.getMajor().get());
    assertFalse(version.getMinor().isPresent());
    assertFalse(version.getMicro().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseStarMajor() {
    String string = "*";
    PartialVersion version = new PartialVersion(string);

    assertFalse(version.getMajor().isPresent());
    assertFalse(version.getMinor().isPresent());
    assertFalse(version.getMicro().isPresent());

    assertEquals(string, version.toString());
  }

  @Test
  public void parseUppercaseXMajor() {
    String string = "X";
    PartialVersion version = new PartialVersion(string);

    assertFalse(version.getMajor().isPresent());
    assertFalse(version.getMinor().isPresent());
    assertFalse(version.getMicro().isPresent());

    assertEquals("*", version.toString());
  }

  @Test
  public void parseLowercaseXMajor() {
    String string = "x";
    PartialVersion version = new PartialVersion(string);

    assertFalse(version.getMajor().isPresent());
    assertFalse(version.getMinor().isPresent());
    assertFalse(version.getMicro().isPresent());

    assertEquals("*", version.toString());
  }

  @Test
  public void parseLargeNumbers() {
    String string = "123.456.789";
    PartialVersion version = new PartialVersion(string);

    assertEquals(123, (int) version.getMajor().get());
    assertEquals(456, (int) version.getMinor().get());
    assertEquals(789, (int) version.getMicro().get());

    assertEquals(string, version.toString());
  }

  @Test
  public void parsePreReleaseWithExtraDashes() {
    String string = "0.0.0-a-b-c";
    PartialVersion version = new PartialVersion(string);

    assertEquals(version.getPreRelease().get().toString(), "a-b-c");

    assertEquals(string, version.toString());
  }
}
