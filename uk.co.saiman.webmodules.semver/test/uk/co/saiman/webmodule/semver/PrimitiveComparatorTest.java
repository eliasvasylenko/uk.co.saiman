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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Injectable;

public class PrimitiveComparatorTest {
  @Test
  public void equalityPositiveTest(@Injectable Version first, @Injectable Version second) {
    new Expectations() {
      {
        first.equals(second);
        result = true;
        second.equals(first);
        result = true;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.EQUAL, first).matches(second));
    assertTrue(new PrimitiveComparator(Operator.EQUAL, second).matches(first));
  }

  @Test
  public void equalityNegativeTest(@Injectable Version first, @Injectable Version second) {
    new Expectations() {
      {
        first.equals(second);
        result = false;
        second.equals(first);
        result = false;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.EQUAL, first).matches(second));
    assertFalse(new PrimitiveComparator(Operator.EQUAL, second).matches(first));
  }

  @Test
  public void greaterThanComparatorForVersionWhichIsHigher(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = -1;
        second.compareTo(first);
        result = 1;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN, first).matches(second));
  }

  @Test
  public void greaterThanComparatorForVersionWhichIsLower(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 1;
        second.compareTo(first);
        result = -1;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN, first).matches(second));
  }

  @Test
  public void greaterThanComparatorForVersionWhichIsEqual(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 0;
        second.compareTo(first);
        result = 0;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN, first).matches(second));
  }

  @Test
  public void greaterThanOrEqualComparatorForVersionWhichIsHigher(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = -1;
        second.compareTo(first);
        result = 1;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, first).matches(second));
  }

  @Test
  public void greaterThanOrEqualComparatorForVersionWhichIsLower(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 1;
        second.compareTo(first);
        result = -1;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, first).matches(second));
  }

  @Test
  public void greaterThanOrEqualComparatorForVersionWhichIsEqual(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 0;
        second.compareTo(first);
        result = 0;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.GREATER_THAN_OR_EQUAL, first).matches(second));
  }

  @Test
  public void lessThanComparatorForVersionWhichIsHigher(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = -1;
        second.compareTo(first);
        result = 1;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.LESS_THAN, first).matches(second));
  }

  @Test
  public void lessThanComparatorForVersionWhichIsLower(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 1;
        second.compareTo(first);
        result = -1;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.LESS_THAN, first).matches(second));
  }

  @Test
  public void lessThanComparatorForVersionWhichIsEqual(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 0;
        second.compareTo(first);
        result = 0;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.LESS_THAN, first).matches(second));
  }

  @Test
  public void lessThanOrEqualComparatorForVersionWhichIsHigher(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = -1;
        second.compareTo(first);
        result = 1;
      }
    };

    assertFalse(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, first).matches(second));
  }

  @Test
  public void lessThanOrEqualComparatorForVersionWhichIsLower(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 1;
        second.compareTo(first);
        result = -1;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, first).matches(second));
  }

  @Test
  public void lessThanOrEqualComparatorForVersionWhichIsEqual(
      @Injectable Version first,
      @Injectable Version second) {
    new Expectations() {
      {
        first.compareTo(second);
        result = 0;
        second.compareTo(first);
        result = 0;
      }
    };

    assertTrue(new PrimitiveComparator(Operator.LESS_THAN_OR_EQUAL, first).matches(second));
  }
}
