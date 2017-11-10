/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.util.Optional;

import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.VoidExecutionContext;
import uk.co.saiman.property.Property;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * The root experiment type implementation for {@link XmlWorkspace}.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentRootImpl implements ExperimentRoot {
  private final ExperimentProperties text;

  protected ExperimentRootImpl(ExperimentProperties text) {
    this.text = text;
  }

  @Override
  public String getName() {
    return text.experimentRoot().toString();
  }

  @Override
  public ExperimentConfiguration createState(
      ConfigurationContext<ExperimentConfiguration> configuration) {
    return new ExperimentConfiguration() {
      private Property<String> notes = configuration.persistedState().stringValue("notes");

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
        return notes.tryGet();
      }

      @Override
      public void setNotes(String notes) {
        this.notes.set(notes);
      }

      @Override
      public void clearNotes() {
        notes = null;
      }
    };
  }

  @Override
  public void executeVoid(VoidExecutionContext<ExperimentConfiguration> context) {}

  @Override
  public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return false;
  }

  @Override
  public boolean mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    return true;
  }

  @Override
  public TypeToken<Void> getResultType() {
    return forType(void.class);
  }
}
