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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.comparator.ComparatorMatcherBuilder;
import org.junit.jupiter.api.Test;

/**
 * It is very difficult or impossible to test this class properly, as the
 * important aspects of its behaviour either only occur in extremely rare
 * circumstances, or change slightly with implementation details of the VM, or
 * both.
 * 
 * @author Elias N Vasylenko
 *
 */
public class IdentityComparatorTest {
  /**
   * This test compares identity equal references.
   */
  @Test
  public void compareIdenticalReferences() {
    Object reference = new Object();
    assertThat(
        "Identical objects should pass identity equality test",
        reference,
        comparator().comparesEqualTo(reference));
  }

  /**
   * This test compares identity unequal references.
   */
  @Test
  public void compareDifferentReferences() {
    assertThat(
        "Equal but distinct objects should fail identity equality test",
        new Object(),
        not(comparator().comparesEqualTo(new Object())));
  }

  private ComparatorMatcherBuilder<Object> comparator() {
    return ComparatorMatcherBuilder.comparedBy(EquivalenceComparator.identityComparator());
  }
}
