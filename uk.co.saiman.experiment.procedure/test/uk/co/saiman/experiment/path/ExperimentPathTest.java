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
 * This file is part of uk.co.saiman.experiment.procedure.
 *
 * uk.co.saiman.experiment.procedure is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.procedure is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.path;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.hamcrest.comparator.ComparatorMatcherBuilder;
import org.junit.jupiter.api.Test;

import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.declaration.ExperimentPath.Relative;

public class ExperimentPathTest {
  @Test
  public void testEmptyAbsolutePath() {
    ExperimentPath<Absolute> path = ExperimentPath.defineAbsolute();

    assertThat(path.ancestorDepth(), equalTo(0));
    assertThat(path.ids().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(true));
  }

  @Test
  public void testEmptyRelativePath() {
    ExperimentPath<Relative> path = ExperimentPath.defineRelative();

    assertThat(path.ancestorDepth(), equalTo(0));
    assertThat(path.ids().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testParentOfEmptyAbsolutePath() {
    Optional<ExperimentPath<Absolute>> path = ExperimentPath.defineAbsolute().parent();

    assertTrue(path.isEmpty());
  }

  @Test
  public void testParentOfEmptyRelativePath() {
    ExperimentPath<Relative> path = ExperimentPath.defineRelative().parent().get();

    assertThat(path.ancestorDepth(), equalTo(1));
    assertThat(path.ids().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testParentOfAbsolutePath() {
    ExperimentPath<Absolute> path = ExperimentPath.defineAbsolute().resolve("id").parent().get();

    assertThat(path.ancestorDepth(), equalTo(0));
    assertThat(path.ids().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(true));
  }

  @Test
  public void testParentOfRelativePath() {
    ExperimentPath<Relative> path = ExperimentPath.defineRelative().resolve("id").parent().get();

    assertThat(path.ancestorDepth(), equalTo(0));
    assertThat(path.ids().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testResolveChildOfPath() {
    ExperimentPath<Relative> path = ExperimentPath.defineRelative().resolve("id");

    assertThat(path.ancestorDepth(), equalTo(0));
    assertThat(path.ids().collect(toList()), contains("id"));
  }

  @Test
  public void testWithAncestorEquality() {
    ExperimentPath<Relative> path1 = ExperimentPath
        .defineRelative()
        .parent()
        .get()
        .parent()
        .get()
        .resolve("true");
    ExperimentPath<Relative> path2 = ExperimentPath
        .defineRelative()
        .parent()
        .get()
        .parent()
        .get()
        .resolve("true");
    ExperimentPath<Relative> path3 = ExperimentPath
        .defineRelative()
        .parent()
        .get()
        .parent()
        .get()
        .resolve("false");
    ExperimentPath<Relative> path4 = ExperimentPath.defineRelative().parent().get().resolve("true");

    assertEquals(path1, path2);
    assertNotEquals(path1, path3);
    assertNotEquals(path3, path1);
    assertNotEquals(path1, path4);
    assertNotEquals(path4, path1);
  }

  @Test
  public void testWithDepthEquality() {
    ExperimentPath<Relative> path1 = ExperimentPath.defineRelative().resolve("a").resolve("b");
    ExperimentPath<Relative> path2 = ExperimentPath.defineRelative().resolve("a").resolve("b");
    ExperimentPath<Relative> path3 = ExperimentPath
        .defineRelative()
        .resolve("a")
        .resolve("b")
        .resolve("c");

    assertEquals(path1, path2);
    assertNotEquals(path1, path3);
    assertNotEquals(path3, path1);
  }

  @Test
  public void testWithAncerstorOrdering() {
    ExperimentPath<Relative> path1 = ExperimentPath
        .defineRelative()
        .parent()
        .get()
        .parent()
        .get()
        .resolve("true");
    ExperimentPath<Relative> path2 = ExperimentPath
        .defineRelative()
        .parent()
        .get()
        .parent()
        .get()
        .resolve("true");
    ExperimentPath<Relative> path3 = ExperimentPath.defineRelative().parent().get().resolve("true");

    assertThat(path1, comparatorMatcher().comparesEqualTo(path2));
    assertThat(path1, comparatorMatcher().lessThan(path3));
    assertThat(path3, comparatorMatcher().greaterThan(path1));
  }

  @Test
  public void testWithDepthOrdering() {
    ExperimentPath<Relative> path1 = ExperimentPath.defineRelative().resolve("a").resolve("b");
    ExperimentPath<Relative> path2 = ExperimentPath.defineRelative().resolve("a").resolve("b");
    ExperimentPath<Relative> path3 = ExperimentPath
        .defineRelative()
        .resolve("a")
        .resolve("b")
        .resolve("c");

    assertThat(path1, comparatorMatcher().comparesEqualTo(path2));
    assertThat(path1, comparatorMatcher().lessThan(path3));
    assertThat(path3, comparatorMatcher().greaterThan(path1));
  }

  @Test
  public void testAtDepthOrdering() {
    ExperimentPath<Relative> path1 = ExperimentPath
        .defineRelative()
        .resolve("a")
        .resolve("b")
        .resolve("c");
    ExperimentPath<Relative> path2 = ExperimentPath
        .defineRelative()
        .resolve("a")
        .resolve("b")
        .resolve("d");

    assertThat(path1, comparatorMatcher().lessThan(path2));
    assertThat(path2, comparatorMatcher().greaterThan(path1));
  }

  public static ComparatorMatcherBuilder<ExperimentPath<?>> comparatorMatcher() {
    return ComparatorMatcherBuilder.<ExperimentPath<?>>usingNaturalOrdering();
  }
}
