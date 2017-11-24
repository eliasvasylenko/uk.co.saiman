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

import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExecutionContext;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.MissingExperimentType;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.reflection.token.TypeToken;

public class MissingExperimentTypeImpl<T> implements MissingExperimentType<T> {
  private final ExperimentProperties text;
  private final String id;

  protected MissingExperimentTypeImpl(ExperimentProperties text, String id) {
    this.text = text;
    this.id = id;
  }

  @Override
  public String getName() {
    return text.missingExperimentType(id).toString();
  }

  public String getMissingTypeID() {
    return id;
  }

  @Override
  public PersistedState createState(ConfigurationContext<PersistedState> context) {
    context.persistedState().forString(getId()).set(getMissingTypeID());

    return context.persistedState();
  }

  @Override
  public T execute(ExecutionContext<PersistedState, T> context) {
    throw new ExperimentException(text.exception().cannotExecuteMissingExperimentType(id));
  }

  @Override
  public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return true;
  }

  @Override
  public boolean mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    return true;
  }

  @Override
  public TypeToken<PersistedState> getStateType() {
    return forType(PersistedState.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public TypeToken<T> getResultType() {
    /*
     * TODO best effort at result type by loading any persisted result data and
     * using the type of that.
     */
    return (TypeToken<T>) forType(void.class);
  }
}
