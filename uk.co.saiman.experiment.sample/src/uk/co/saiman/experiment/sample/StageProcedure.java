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
 * This file is part of uk.co.saiman.experiment.sample.
 *
 * uk.co.saiman.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sample;

import uk.co.saiman.experiment.ProcedureContext;
import uk.co.saiman.instrument.stage.PolarStage;
import uk.co.saiman.instrument.stage.Stage;

/**
 * An {@link SampleProcedure experiment type} for {@link PolarStage radial stage
 * devices}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of sample configuration for the instrument
 */
public interface StageProcedure<T, U extends StageConfiguration<T>> extends SampleProcedure<U> {
  @Override
  Stage<T, ?> sampleDevice();

  @Override
  default void proceed(ProcedureContext<U> context) {
    T location = context.node().getVariables().location();

    try (var control = sampleDevice().acquireControl()) {
      control.requestAnalysisLocation(location);
      context.prepareCondition(getSamplePreparation(), null);
    }
  }
}
