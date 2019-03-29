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

import java.util.stream.Stream;

import uk.co.saiman.experiment.procedure.ConductionContext;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.NoRequirement;
import uk.co.saiman.experiment.procedure.Requirement;
import uk.co.saiman.experiment.procedure.Requirements;
import uk.co.saiman.experiment.product.Nothing;
import uk.co.saiman.experiment.product.Preparation;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.variables.Variable;
import uk.co.saiman.experiment.variables.VariableDeclaration;
import uk.co.saiman.instrument.sample.SampleDevice;

/**
 * Configure the sample position to perform an experiment at. Typically most
 * other experiment nodes will be descendant to a sample experiment node, such
 * that they operate on the configured sample.
 * 
 * @author Elias N Vasylenko
 */
public interface SampleConductor<T> extends Conductor<Nothing> {
  Variable<T> sampleLocation();

  Preparation<Void> samplePreparation();

  SampleDevice<T, ?> sampleDevice();

  @Override
  default void conduct(ConductionContext<Nothing> context) {
    T location = context.getVariable(sampleLocation());

    try (var control = sampleDevice().acquireControl()) {
      control.requestAnalysisLocation(location);
      context.prepareCondition(samplePreparation(), null);
    }
  }

  @Override
  default Stream<Production<?>> products() {
    return Stream.of(samplePreparation());
  }

  @Override
  default Stream<VariableDeclaration> variables() {
    return Stream.of(sampleLocation().declareRequired());
  }

  @Override
  default NoRequirement directRequirement() {
    return Requirement.none();
  }

  @Override
  default Stream<Requirements> indirectRequirements() {
    return Stream.empty();
  }
}
