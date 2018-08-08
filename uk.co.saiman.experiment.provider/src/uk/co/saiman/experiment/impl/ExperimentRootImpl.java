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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static uk.co.saiman.experiment.state.Accessor.stringAccessor;

import java.util.Optional;

import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.VoidExecutionContext;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;

/**
 * @author Elias N Vasylenko
 */
public class ExperimentRootImpl implements ExperimentRoot {
  private static final PropertyAccessor<String> NOTES = stringAccessor("notes");

  private final ExperimentProperties text;

  public ExperimentRootImpl(ExperimentProperties text) {
    this.text = text;
  }

  @Override
  public String getName() {
    return text.experimentRoot().toString();
  }

  @Override
  public ExperimentConfiguration createState(
      ConfigurationContext<ExperimentConfiguration> configuration) {
    configuration.state().withDefault(NOTES, "");

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
        String notes = configuration.state().get(NOTES);
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
  public void executeVoid(VoidExecutionContext<ExperimentConfiguration> context) {
    context.processChildren();
  }

  @Override
  public boolean mayComeAfter(ExperimentType<?, ?> parentType) {
    return false;
  }
}
