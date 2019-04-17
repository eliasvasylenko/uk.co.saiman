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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PrimitiveComparatorTest {
  @Test
  public void comparatorsForVersionWhichIsHigher(@Mock Version first, @Mock Version second) {
    when(first.compareTo(second)).thenReturn(-1);
    when(second.compareTo(first)).thenReturn(1);

    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN, first).matches(second));
    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, first).matches(second));
    assertFalse(new PrimitiveComparator(Operator.LESS_THAN, first).matches(second));
    assertFalse(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, first).matches(second));

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN, second).matches(first));
    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, second).matches(first));
    assertTrue(new PrimitiveComparator(Operator.LESS_THAN, second).matches(first));
    assertTrue(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, second).matches(first));
  }

  @Test
  public void comparatorsForVersionWhichIsLower(@Mock Version first, @Mock Version second) {
    when(first.compareTo(second)).thenReturn(1);
    when(second.compareTo(first)).thenReturn(-1);

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN, first).matches(second));
    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, first).matches(second));
    assertTrue(new PrimitiveComparator(Operator.LESS_THAN, first).matches(second));
    assertTrue(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, first).matches(second));

    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN, second).matches(first));
    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, second).matches(first));
    assertFalse(new PrimitiveComparator(Operator.LESS_THAN, second).matches(first));
    assertFalse(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, second).matches(first));
  }

  @Test
  public void greaterThanComparatorForVersionWhichIsEqual(
      @Mock Version first,
      @Mock Version second) {
    when(first.compareTo(second)).thenReturn(0);
    when(second.compareTo(first)).thenReturn(0);

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN, first).matches(second));
    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, first).matches(second));
    assertFalse(new PrimitiveComparator(Operator.LESS_THAN, first).matches(second));
    assertTrue(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, first).matches(second));

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN, second).matches(first));
    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, second).matches(first));
    assertFalse(new PrimitiveComparator(Operator.LESS_THAN, second).matches(first));
    assertTrue(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, second).matches(first));
  }
}
