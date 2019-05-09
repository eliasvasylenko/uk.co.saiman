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
 * This file is part of uk.co.saiman.experiment.graph.
 *
 * uk.co.saiman.experiment.graph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.graph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.production;

/**
 * A preparation of a condition by an experiment procedure.
 * 
 * @author Elias N Vasylenko
 * 
 * @param <T> The type of condition prepared
 */
public class Preparation<T> extends Production<Condition<T>> {
  public enum Evaluation {
    ORDERED, UNORDERED, PARALLEL
  }

  private final String id;
  private final Class<T> type;
  private final Evaluation evaluation;

  public Preparation(String id, Class<T> type) {
    this(id, type, Evaluation.ORDERED);
  }

  public Preparation(String id, Class<T> type, Evaluation evaluation) {
    this.id = id;
    this.type = type;
    this.evaluation = evaluation;
  }

  @Override
  public String id() {
    return id;
  }

  public Class<T> type() {
    return type;
  }

  public Evaluation evaluation() {
    return evaluation;
  }
}
