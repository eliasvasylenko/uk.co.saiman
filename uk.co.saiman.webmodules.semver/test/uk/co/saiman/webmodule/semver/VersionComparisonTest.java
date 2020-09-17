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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class VersionComparisonTest {
  @Test
  public void equalRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0);
    second = new Version(0, 0, 0);
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);

    first = new Version(1, 2, 3);
    second = new Version(1, 2, 3);
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);

    first = new Version(123, 456, 789);
    second = new Version(123, 456, 789);
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);
  }

  @Test
  public void equalPreRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withPreRelease(new String("0"));
    second = new Version(0, 0, 0).withPreRelease(new String("0"));
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);

    first = new Version(1, 2, 3).withPreRelease(new String("4"));
    second = new Version(1, 2, 3).withPreRelease(new String("4"));
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);

    first = new Version(123, 456, 789).withPreRelease(new String("abc"));
    second = new Version(123, 456, 789).withPreRelease(new String("abc"));
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);
  }

  @Test
  public void higherByAll() {
    Version first;
    Version second;

    first = new Version(1, 1, 1);
    second = new Version(0, 0, 0);
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(1, 1, 1).withPreRelease(new String("b"));
    second = new Version(0, 0, 0).withPreRelease(new String("a"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(1, 1, 1).withBuildInformation(new String("b"));
    second = new Version(0, 0, 0).withBuildInformation(new String("a"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void lowerByAll() {
    Version first;
    Version second;

    first = new Version(0, 0, 0);
    second = new Version(1, 1, 1);
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 0).withPreRelease(new String("a"));
    second = new Version(1, 1, 1).withPreRelease(new String("b"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 0).withBuildInformation(new String("a"));
    second = new Version(1, 1, 1).withBuildInformation(new String("b"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void higherByMajor() {
    Version first;
    Version second;

    first = new Version(1, 0, 0);
    second = new Version(0, 1, 1);
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(1, 0, 0).withPreRelease(new String("a"));
    second = new Version(0, 1, 1).withPreRelease(new String("b"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(1, 0, 0).withBuildInformation(new String("a"));
    second = new Version(0, 1, 1).withBuildInformation(new String("b"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void lowerByMajor() {
    Version first;
    Version second;

    first = new Version(0, 1, 1);
    second = new Version(1, 0, 0);
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 1, 1).withPreRelease(new String("b"));
    second = new Version(1, 0, 0).withPreRelease(new String("a"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 1, 1).withBuildInformation(new String("b"));
    second = new Version(1, 0, 0).withBuildInformation(new String("a"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void higherByMinor() {
    Version first;
    Version second;

    first = new Version(0, 1, 0);
    second = new Version(0, 0, 1);
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 1, 0).withPreRelease(new String("a"));
    second = new Version(0, 0, 1).withPreRelease(new String("b"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 1, 0).withBuildInformation(new String("a"));
    second = new Version(0, 0, 1).withBuildInformation(new String("b"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void lowerByMinor() {
    Version first;
    Version second;

    first = new Version(0, 0, 1);
    second = new Version(0, 1, 0);
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 1).withPreRelease(new String("b"));
    second = new Version(0, 1, 0).withPreRelease(new String("a"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 1).withBuildInformation(new String("b"));
    second = new Version(0, 1, 0).withBuildInformation(new String("a"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void higherByMicro() {
    Version first;
    Version second;

    first = new Version(0, 0, 1);
    second = new Version(0, 0, 0);
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 1).withPreRelease(new String("a"));
    second = new Version(0, 0, 0).withPreRelease(new String("b"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 1).withBuildInformation(new String("a"));
    second = new Version(0, 0, 0).withBuildInformation(new String("b"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void lowerByMicro() {
    Version first;
    Version second;

    first = new Version(0, 0, 0);
    second = new Version(0, 0, 1);
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 0).withPreRelease(new String("b"));
    second = new Version(0, 0, 1).withPreRelease(new String("a"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);

    first = new Version(0, 0, 0).withBuildInformation(new String("b"));
    second = new Version(0, 0, 1).withBuildInformation(new String("a"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void higherByPreRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withPreRelease(new String("b"));
    second = new Version(0, 0, 0).withPreRelease(new String("a"));
    assertEquals(1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void lowerByPreRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withPreRelease(new String("a"));
    second = new Version(0, 0, 0).withPreRelease(new String("b"));
    assertEquals(-1, first.compareTo(second));
    assertNotEquals(first, second);
  }

  @Test
  public void higherByBuildInformationIsIgnored() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withBuildInformation(new String("b"));
    second = new Version(0, 0, 0).withBuildInformation(new String("a"));
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);
  }

  @Test
  public void lowerByBuildInformationIsIgnored() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withBuildInformation(new String("a"));
    second = new Version(0, 0, 0).withBuildInformation(new String("b"));
    assertEquals(0, first.compareTo(second));
    assertEquals(first, second);
  }
}
