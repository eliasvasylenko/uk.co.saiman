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
 * This file is part of uk.co.saiman.saint.
 *
 * uk.co.saiman.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.saint.impl;

import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static uk.co.saiman.experiment.state.Accessor.listAccessor;
import static uk.co.saiman.measurement.Units.dalton;

import java.util.Random;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.experiment.ExperimentContext;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.sample.XYStageProcedure;
import uk.co.saiman.experiment.spectrum.SpectrumProcedure;
import uk.co.saiman.experiment.state.Accessor;
import uk.co.saiman.saint.SaintSpectrumConfiguration;
import uk.co.saiman.saint.SaintXYStageConfiguration;

@Component
public class SaintSpectrumProcedure extends SpectrumProcedure<SaintSpectrumConfiguration>
    implements Procedure<SaintSpectrumConfiguration, Spectrum> {
  @Reference
  private XYStageProcedure<SaintXYStageConfiguration> stageExperiment;

  @Reference(cardinality = OPTIONAL)
  private AcquisitionDevice acquisitionDevice;

  @Reference
  private ProcessingService processors;

  @Override
  protected Unit<Mass> getMassUnit() {
    return dalton().getUnit();
  }

  @Override
  public SaintSpectrumConfiguration configureVariables(
      ExperimentContext<SaintSpectrumConfiguration> context) {
    Accessor<Processing, ?> accessor = listAccessor(
        "processing",
        processors::loadProcessing,
        processors::saveProcessing);

    context.update(s -> s.withDefault(accessor, Processing::new));

    return new SaintSpectrumConfiguration() {
      private String name = context.getId(() -> "test-" + new Random().nextInt(Integer.MAX_VALUE));

      @Override
      public void setSpectrumName(String name) {
        this.name = name;
        context.setId(name);
      }

      @Override
      public String getSpectrumName() {
        return name;
      }

      @Override
      public AcquisitionDevice getAcquisitionDevice() {
        return acquisitionDevice;
      }

      @Override
      public Processing getProcessing() {
        return context.stateMap().get(accessor);
      }

      @Override
      public void setProcessing(Processing processing) {
        context.update(state -> state.with(accessor, processing));
      }
    };
  }

  @Override
  public boolean mayComeAfter(Procedure<?, ?> parentType) {
    return parentType == stageExperiment;
  }

  @Override
  public AcquisitionDevice getAcquisitionDevice() {
    return acquisitionDevice;
  }
}
