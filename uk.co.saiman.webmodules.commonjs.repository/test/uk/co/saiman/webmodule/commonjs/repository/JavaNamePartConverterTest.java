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
 * This file is part of uk.co.saiman.webmodules.commonjs.repository.
 *
 * uk.co.saiman.webmodules.commonjs.repository is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs.repository is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.repository;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.co.saiman.webmodule.commonjs.repository.CommonJsJar;

public class JavaNamePartConverterTest {
  @Test
  public void splitOnDot() {
    List<String> parts = CommonJsJar.getJavaNameParts("a.b", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "b"), parts);
  }

  @Test
  public void splitOnUnderscore() {
    List<String> parts = CommonJsJar.getJavaNameParts("a_b", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "b"), parts);
  }

  @Test
  public void splitOnHyphen() {
    List<String> parts = CommonJsJar.getJavaNameParts("a-b", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "b"), parts);
  }

  @Test
  public void prependWithUnderscoreWhenStartWithNumber() {
    List<String> parts = CommonJsJar.getJavaNameParts("1abc", false).collect(toList());

    Assert.assertEquals(Arrays.asList("_1abc"), parts);
  }

  @Test
  public void prependSecondPartWithUnderscoreWhenStartWithNumberAndSeparated() {
    List<String> parts = CommonJsJar.getJavaNameParts("a.1abc", true).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "_1abc"), parts);
  }

  @Test
  public void doNotPrependSecondPartWhenStartWithNumberAndNotSeparated() {
    List<String> parts = CommonJsJar.getJavaNameParts("a.1abc", false).collect(toList());

    Assert.assertEquals(Arrays.asList("a", "1abc"), parts);
  }
}
