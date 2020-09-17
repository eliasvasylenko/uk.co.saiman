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
 * This file is part of uk.co.saiman.collections.
 *
 * uk.co.saiman.collections is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.collections is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class StreamUtilitiesTest {
  interface A {}

  interface B extends A {}

  interface C extends B {}

  interface D extends B, C {}

  @Test
  public void flatMapSingleLevelTest() {
    assertEquals(
        Arrays.asList(B.class, A.class),
        StreamUtilities
            .<Class<?>>flatMapRecursive(B.class, c -> Stream.of(c.getInterfaces()))
            .collect(Collectors.toList()));
  }

  @Test
  public void flatMapTwoLevelsTest() {
    assertEquals(
        Arrays.asList(C.class, B.class, A.class),
        StreamUtilities
            .<Class<?>>flatMapRecursive(C.class, c -> Stream.of(c.getInterfaces()))
            .collect(Collectors.toList()));
  }

  @Test
  public void flatMapThreeLevelsRepeatsTest() {
    assertEquals(
        Arrays.asList(D.class, B.class, A.class, C.class, B.class, A.class),
        StreamUtilities
            .<Class<?>>flatMapRecursive(D.class, c -> Stream.of(c.getInterfaces()))
            .collect(Collectors.toList()));
  }

  @Test
  public void flatMapThreeLevelsDistinctTest() {
    assertEquals(
        Arrays.asList(D.class, B.class, A.class, C.class),
        StreamUtilities
            .<Class<?>>flatMapRecursiveDistinct(D.class, c -> Stream.of(c.getInterfaces()))
            .collect(Collectors.toList()));
  }
}
