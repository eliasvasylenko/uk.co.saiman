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

public class PreReleaseVersionStringTest {
  @Test(expected = IllegalArgumentException.class)
  public void parseWithInnerSpace() {
    new PreReleaseVersion("0 0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseWithLeadingSpace() {
    new PreReleaseVersion(" 0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parseMinorWithTrailingSpace() {
    new PreReleaseVersion("0 ");
  }

  @Test
  public void parseMultipleZeroRelease() {
    String string = "0.0.0.0";
    PreReleaseVersion version = new PreReleaseVersion(string);

    Assert
        .assertEquals(
            0,
            (int) version.getIdentifiers().skip(0).findFirst().get().getInteger().get());
    Assert
        .assertEquals(
            0,
            (int) version.getIdentifiers().skip(1).findFirst().get().getInteger().get());
    Assert
        .assertEquals(
            0,
            (int) version.getIdentifiers().skip(2).findFirst().get().getInteger().get());
    Assert
        .assertEquals(
            0,
            (int) version.getIdentifiers().skip(3).findFirst().get().getInteger().get());

    Assert.assertEquals(4, version.getIdentifiers().count());

    Assert.assertEquals(string, version.toString());
  }

  @Test
  public void parseMultipleLetterRelease() {
    String string = "a.b.c.d";
    PreReleaseVersion version = new PreReleaseVersion(string);

    Assert.assertEquals("a", version.getIdentifiers().skip(0).findFirst().get().getString().get());
    Assert.assertEquals("b", version.getIdentifiers().skip(1).findFirst().get().getString().get());
    Assert.assertEquals("c", version.getIdentifiers().skip(2).findFirst().get().getString().get());
    Assert.assertEquals("d", version.getIdentifiers().skip(3).findFirst().get().getString().get());

    Assert.assertEquals(4, version.getIdentifiers().count());

    Assert.assertEquals(string, version.toString());
  }
}
