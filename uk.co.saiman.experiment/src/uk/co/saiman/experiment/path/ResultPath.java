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

import static uk.co.saiman.experiment.path.ExperimentPath.SEPARATOR;

import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Result;

public class ResultPath<T> {
  private final ExperimentPath experimentPath;
  private final ResultMatcher<T> matcher;

  protected ResultPath(ExperimentPath containingExperiment, ResultMatcher<T> matcher) {
    this.experimentPath = containingExperiment;
    this.matcher = matcher;
  }

  public static <T> ResultPath<T> of(Result<T> result) {
    return new ResultPath<T>(
        ExperimentPath.of(result.getExperimentStep()),
        ResultMatcher.matching(result.getObservation()));
  }

  public static ResultPath<?> fromString(String string) {
    int pathEnd = string.lastIndexOf(SEPARATOR) + 1;

    ExperimentPath experimentPath = ExperimentPath.fromString(string.substring(0, pathEnd));
    ResultMatcher<?> resultMatcher = ResultMatcher.fromString(string.substring(pathEnd + 1));

    return new ResultPath<>(experimentPath, resultMatcher);
  }

  @Override
  public String toString() {
    return experimentPath.toString() + SEPARATOR + matcher.toString();
  }

  @SuppressWarnings("unchecked")
  private static <T> Result<? extends T> resolve(
      ExperimentStep<?> experimentStep,
      ResultMatcher<T> matcher) {
    return (Result<T>) experimentStep.getResults().filter(matcher::match).findFirst().get();
  }

  public Result<? extends T> resolve(ExperimentIndex index, ExperimentStep<?> node) {
    return resolve(experimentPath.resolve(index, node), matcher);
  }

  public Result<? extends T> resolve(ExperimentStep<?> node) {
    return resolve(experimentPath.resolve(node), matcher);
  }

  public Result<? extends T> resolve(ExperimentIndex index) {
    return resolve(experimentPath.resolve(index), matcher);
  }

  public ExperimentPath getExperimentPath() {
    return experimentPath;
  }

  public ResultMatcher<T> getMatcher() {
    return matcher;
  }
}
