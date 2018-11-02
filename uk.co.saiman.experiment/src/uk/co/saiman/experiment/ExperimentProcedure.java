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

import static uk.co.saiman.experiment.state.Accessor.stringAccessor;

import java.util.Optional;

import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;

public class ExperimentProcedure implements VoidProcedure<ExperimentConfiguration> {
  public static final String FULL_PROCEDURE_PID = "uk.co.saiman.experiment.procedure.full";

  private static final PropertyAccessor<String> NOTES = stringAccessor("notes");
  private static final ExperimentProcedure INSTANCE = new ExperimentProcedure();

  @Override
  public ExperimentConfiguration configureVariables(
      ExperimentContext<ExperimentConfiguration> configuration) {
    configuration.stateMap().withDefault(NOTES, () -> "");

    return new ExperimentConfiguration() {
      @Override
      public String getName() {
        return configuration.node().getId();
      }

      @Override
      public void setName(String name) {
        configuration.setId(name);
      }

      @Override
      public Optional<String> getNotes() {
        String notes = configuration.stateMap().get(NOTES);
        return notes.isEmpty() ? Optional.empty() : Optional.of(notes);
      }

      @Override
      public void setNotes(String notes) {
        configuration.update(state -> state.with(NOTES, notes));
      }

      @Override
      public void clearNotes() {
        configuration.update(state -> state.remove(NOTES));
      }
    };
  }

  @Override
  public void executeVoid(VoidProcedureContext<ExperimentConfiguration> context) {
    context.processChildren();
  }

  @Override
  public boolean mayComeAfter(Procedure<?, ?> parentType) {
    return false;
  }

  public static ExperimentProcedure instance() {
    return INSTANCE;
  }
}
