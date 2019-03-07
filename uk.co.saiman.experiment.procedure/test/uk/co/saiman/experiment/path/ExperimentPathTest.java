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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
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
import static uk.co.saiman.experiment.path.ExperimentPath.defineAbsolute;
import static uk.co.saiman.experiment.path.ExperimentPath.defineRelative;

import java.util.Optional;

import org.hamcrest.comparator.ComparatorMatcherBuilder;
import org.junit.jupiter.api.Test;

public class ExperimentPathTest {
  @Test
  public void testEmptyAbsolutePath() {
    ExperimentPath path = defineAbsolute();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getIds().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(true));
  }

  @Test
  public void testEmptyRelativePath() {
    ExperimentPath path = defineRelative();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getIds().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testParentOfEmptyAbsolutePath() {
    Optional<ExperimentPath> path = defineAbsolute().parent();

    assertTrue(path.isEmpty());
  }

  @Test
  public void testParentOfEmptyRelativePath() {
    ExperimentPath path = defineRelative().parent().get();

    assertThat(path.getAncestorDepth(), equalTo(1));
    assertThat(path.getIds().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testParentOfAbsolutePath() {
    ExperimentPath path = defineAbsolute().resolve("id").parent().get();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getIds().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(true));
  }

  @Test
  public void testParentOfRelativePath() {
    ExperimentPath path = defineRelative().resolve("id").parent().get();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getIds().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testResolveChildOfPath() {
    ExperimentPath path = defineRelative().resolve("id");

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getIds().collect(toList()), contains("id"));
  }

  @Test
  public void testWithAncestorEquality() {
    ExperimentPath path1 = defineRelative().parent().get().parent().get().resolve("true");
    ExperimentPath path2 = defineRelative().parent().get().parent().get().resolve("true");
    ExperimentPath path3 = defineRelative().parent().get().parent().get().resolve("false");
    ExperimentPath path4 = defineRelative().parent().get().resolve("true");

    assertEquals(path1, path2);
    assertNotEquals(path1, path3);
    assertNotEquals(path3, path1);
    assertNotEquals(path1, path4);
    assertNotEquals(path4, path1);
  }

  @Test
  public void testWithDepthEquality() {
    ExperimentPath path1 = defineRelative().resolve("a").resolve("b");
    ExperimentPath path2 = defineRelative().resolve("a").resolve("b");
    ExperimentPath path3 = defineRelative().resolve("a").resolve("b").resolve("c");

    assertEquals(path1, path2);
    assertNotEquals(path1, path3);
    assertNotEquals(path3, path1);
  }

  @Test
  public void testWithAncerstorOrdering() {
    ExperimentPath path1 = defineRelative().parent().get().parent().get().resolve("true");
    ExperimentPath path2 = defineRelative().parent().get().parent().get().resolve("true");
    ExperimentPath path3 = defineRelative().parent().get().resolve("true");

    assertThat(
        path1,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().comparesEqualTo(path2));
    assertThat(
        path1,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().lessThan(path3));
    assertThat(
        path3,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().greaterThan(path1));
  }

  @Test
  public void testWithDepthOrdering() {
    ExperimentPath path1 = defineRelative().resolve("a").resolve("b");
    ExperimentPath path2 = defineRelative().resolve("a").resolve("b");
    ExperimentPath path3 = defineRelative().resolve("a").resolve("b").resolve("c");

    assertThat(
        path1,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().comparesEqualTo(path2));
    assertThat(
        path1,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().lessThan(path3));
    assertThat(
        path3,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().greaterThan(path1));
  }

  @Test
  public void testAtDepthOrdering() {
    ExperimentPath path1 = defineRelative().resolve("a").resolve("b").resolve("c");
    ExperimentPath path2 = defineRelative().resolve("a").resolve("b").resolve("d");

    assertThat(
        path1,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().lessThan(path2));
    assertThat(
        path2,
        ComparatorMatcherBuilder.<ExperimentPath>usingNaturalOrdering().greaterThan(path1));
  }
}
