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

import uk.co.saiman.webmodule.semver.Version;

public class VersionComparisonTest {
  @Test
  public void equalRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0);
    second = new Version(0, 0, 0);
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);

    first = new Version(1, 2, 3);
    second = new Version(1, 2, 3);
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);

    first = new Version(123, 456, 789);
    second = new Version(123, 456, 789);
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);
  }

  @Test
  public void equalPreRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withPreRelease(new String("0"));
    second = new Version(0, 0, 0).withPreRelease(new String("0"));
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);

    first = new Version(1, 2, 3).withPreRelease(new String("4"));
    second = new Version(1, 2, 3).withPreRelease(new String("4"));
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);

    first = new Version(123, 456, 789).withPreRelease(new String("abc"));
    second = new Version(123, 456, 789).withPreRelease(new String("abc"));
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);
  }

  @Test
  public void higherByAll() {
    Version first;
    Version second;

    first = new Version(1, 1, 1);
    second = new Version(0, 0, 0);
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(1, 1, 1).withPreRelease(new String("b"));
    second = new Version(0, 0, 0).withPreRelease(new String("a"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(1, 1, 1).withBuildInformation(new String("b"));
    second = new Version(0, 0, 0).withBuildInformation(new String("a"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void lowerByAll() {
    Version first;
    Version second;

    first = new Version(0, 0, 0);
    second = new Version(1, 1, 1);
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 0).withPreRelease(new String("a"));
    second = new Version(1, 1, 1).withPreRelease(new String("b"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 0).withBuildInformation(new String("a"));
    second = new Version(1, 1, 1).withBuildInformation(new String("b"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void higherByMajor() {
    Version first;
    Version second;

    first = new Version(1, 0, 0);
    second = new Version(0, 1, 1);
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(1, 0, 0).withPreRelease(new String("a"));
    second = new Version(0, 1, 1).withPreRelease(new String("b"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(1, 0, 0).withBuildInformation(new String("a"));
    second = new Version(0, 1, 1).withBuildInformation(new String("b"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void lowerByMajor() {
    Version first;
    Version second;

    first = new Version(0, 1, 1);
    second = new Version(1, 0, 0);
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 1, 1).withPreRelease(new String("b"));
    second = new Version(1, 0, 0).withPreRelease(new String("a"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 1, 1).withBuildInformation(new String("b"));
    second = new Version(1, 0, 0).withBuildInformation(new String("a"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void higherByMinor() {
    Version first;
    Version second;

    first = new Version(0, 1, 0);
    second = new Version(0, 0, 1);
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 1, 0).withPreRelease(new String("a"));
    second = new Version(0, 0, 1).withPreRelease(new String("b"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 1, 0).withBuildInformation(new String("a"));
    second = new Version(0, 0, 1).withBuildInformation(new String("b"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void lowerByMinor() {
    Version first;
    Version second;

    first = new Version(0, 0, 1);
    second = new Version(0, 1, 0);
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 1).withPreRelease(new String("b"));
    second = new Version(0, 1, 0).withPreRelease(new String("a"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 1).withBuildInformation(new String("b"));
    second = new Version(0, 1, 0).withBuildInformation(new String("a"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void higherByMicro() {
    Version first;
    Version second;

    first = new Version(0, 0, 1);
    second = new Version(0, 0, 0);
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 1).withPreRelease(new String("a"));
    second = new Version(0, 0, 0).withPreRelease(new String("b"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 1).withBuildInformation(new String("a"));
    second = new Version(0, 0, 0).withBuildInformation(new String("b"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void lowerByMicro() {
    Version first;
    Version second;

    first = new Version(0, 0, 0);
    second = new Version(0, 0, 1);
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 0).withPreRelease(new String("b"));
    second = new Version(0, 0, 1).withPreRelease(new String("a"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);

    first = new Version(0, 0, 0).withBuildInformation(new String("b"));
    second = new Version(0, 0, 1).withBuildInformation(new String("a"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void higherByPreRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withPreRelease(new String("b"));
    second = new Version(0, 0, 0).withPreRelease(new String("a"));
    Assert.assertEquals(1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void lowerByPreRelease() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withPreRelease(new String("a"));
    second = new Version(0, 0, 0).withPreRelease(new String("b"));
    Assert.assertEquals(-1, first.compareTo(second));
    Assert.assertNotEquals(first, second);
  }

  @Test
  public void higherByBuildInformationIsIgnored() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withBuildInformation(new String("b"));
    second = new Version(0, 0, 0).withBuildInformation(new String("a"));
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);
  }

  @Test
  public void lowerByBuildInformationIsIgnored() {
    Version first;
    Version second;

    first = new Version(0, 0, 0).withBuildInformation(new String("a"));
    second = new Version(0, 0, 0).withBuildInformation(new String("b"));
    Assert.assertEquals(0, first.compareTo(second));
    Assert.assertEquals(first, second);
  }
}
