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
package uk.co.saiman.experiment;

import static java.lang.String.format;

import java.util.stream.Stream;

import uk.co.saiman.experiment.state.StateMap;
import uk.co.saiman.reflection.token.TypeToken;

public class UnknownProcedure implements Procedure<StateMap> {
  private final String id;

  public UnknownProcedure(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public StateMap configureVariables(ExperimentContext<StateMap> context) {
    return context.stateMap();
  }

  @Override
  public TypeToken<StateMap> getVariablesType() {
    return new TypeToken<StateMap>() {};
  }

  @Override
  public void proceed(ProcedureContext<StateMap> context) {
    throw new ExperimentException(format("Cannot execute missing experiment type %s", id));
  }

  @Override
  public Stream<Condition> expectations() {
    return Stream.empty();
  }

  @Override
  public Stream<Condition> conditions() {
    return Stream.empty();
  }

  @Override
  public Stream<Dependency<?>> dependencies() {
    return Stream.empty();
  }

  @Override
  public Stream<Observation<?>> observations() {
    return Stream.empty(); // TODO load existing results;
  }
}
