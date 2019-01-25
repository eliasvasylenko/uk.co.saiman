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
import static uk.co.saiman.experiment.path.ExperimentPath.absolute;
import static uk.co.saiman.experiment.path.ExperimentPath.relative;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentStep;

public class ExperimentPathTest {
  @Mock
  ExperimentStep<?> node;

  @Test
  public void testEmptyAbsolutePath() {
    ExperimentPath path = absolute();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getMatchers().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(true));
  }

  @Test
  public void testEmptyRelativePath() {
    ExperimentPath path = relative();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getMatchers().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testParentOfEmptyAbsolutePath() {
    Assertions.assertThrows(ExperimentException.class, absolute()::parent);
  }

  @Test
  public void testParentOfEmptyRelativePath() {
    ExperimentPath path = relative().parent();

    assertThat(path.getAncestorDepth(), equalTo(1));
    assertThat(path.getMatchers().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testParentOfAbsolutePath(@Mock ExperimentMatcher matcher) {
    ExperimentPath path = absolute().resolve(matcher).parent();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getMatchers().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(true));
  }

  @Test
  public void testParentOfRelativePath(@Mock ExperimentMatcher matcher) {
    ExperimentPath path = relative().resolve(matcher).parent();

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getMatchers().collect(toList()), empty());
    assertThat(path.isAbsolute(), is(false));
  }

  @Test
  public void testResolveChildOfPath(@Mock ExperimentMatcher matcher) {
    ExperimentPath path = relative().resolve(matcher);

    assertThat(path.getAncestorDepth(), equalTo(0));
    assertThat(path.getMatchers().collect(toList()), contains(matcher));
  }
}
