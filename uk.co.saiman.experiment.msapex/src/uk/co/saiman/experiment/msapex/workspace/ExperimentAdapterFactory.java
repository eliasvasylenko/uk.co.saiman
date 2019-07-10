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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.workspace;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.Step;

public class ExperimentAdapterFactory implements IAdapterFactory {
  private final IAdapterManager adapterManager;
  private final ExperimentStepAdapterFactory experimentNodeAdapterFactory;

  public ExperimentAdapterFactory(
      IAdapterManager adapterManager,
      ExperimentStepAdapterFactory experimentNodeAdapterFactory) {
    this.adapterManager = adapterManager;
    this.experimentNodeAdapterFactory = experimentNodeAdapterFactory;
    adapterManager.registerAdapters(this, Experiment.class);
  }

  public void unregister() {
    adapterManager.unregisterAdapters(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
    Experiment experiment = (Experiment) adaptableObject;

    if (adapterType == Step.class) {
      return (T) experiment;
    }

    return experimentNodeAdapterFactory.getAdapter(adaptableObject, adapterType);
  }

  @Override
  public Class<?>[] getAdapterList() {
    return concat(of(Step.class), of(experimentNodeAdapterFactory.getAdapterList()))
        .toArray(Class[]::new);
  }
}
