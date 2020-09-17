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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.workspace;

import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;

public class WorkspaceExperimentPath implements Comparable<WorkspaceExperimentPath> {
  private final ExperimentId experimentIndex;
  private final ExperimentPath<Absolute> experimentPath;

  WorkspaceExperimentPath(ExperimentId experimentIndex, ExperimentPath<Absolute> experimentPath) {
    this.experimentIndex = experimentIndex;
    this.experimentPath = experimentPath;
  }

  public static WorkspaceExperimentPath define(
      ExperimentId experimentIndex,
      ExperimentPath<Absolute> experimentPath) {
    return new WorkspaceExperimentPath(experimentIndex, experimentPath);
  }

  public ExperimentId getExperimentId() {
    return experimentIndex;
  }

  public ExperimentPath<Absolute> getExperimentPath() {
    return experimentPath;
  }

  public static WorkspaceExperimentPath fromString(String string) {
    string = string.strip();

    int lastSlash = string.lastIndexOf('/');

    return define(
        ExperimentId.fromName(string.substring(lastSlash + 1)),
        ExperimentPath.absoluteFromString(string.substring(0, lastSlash)));
  }

  @Override
  public String toString() {
    return experimentIndex.toString() + experimentPath.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (WorkspaceExperimentPath) obj;

    return Objects.equals(this.experimentIndex, that.experimentIndex)
        && Objects.equals(this.experimentPath, that.experimentPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(experimentIndex, experimentPath);
  }

  @Override
  public int compareTo(WorkspaceExperimentPath that) {
    int compareIndex = this.experimentIndex.compareTo(that.experimentIndex);
    return compareIndex != 0 ? compareIndex : this.experimentPath.compareTo(that.experimentPath);
  }

  public Stream<ExperimentId> ids() {
    return Stream.concat(Stream.of(experimentIndex), experimentPath.ids());
  }
}
