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
import static uk.co.saiman.measurement.Units.dalton;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.procedure.ConditionRequirement;
import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.Requirement;
import uk.co.saiman.experiment.processing.ProcessingService;
import uk.co.saiman.experiment.product.Condition;
import uk.co.saiman.experiment.sample.XYStageConductor;
import uk.co.saiman.experiment.spectrum.SpectrumConductor;

@Component
public class SaintSpectrumConductor implements SpectrumConductor, Conductor<Condition<Void>> {
  public static final String SAINT_SPECTRUM = "uk.co.saiman.saint.spectrum.result";

  private final AcquisitionDevice<?> acquisitionDevice;
  private final ConditionRequirement<Void> sampleResource;
  private final ProcessingService processingService;

  @Activate
  public SaintSpectrumConductor(
      @Reference XYStageConductor stageExperiment,
      @Reference(cardinality = OPTIONAL) AcquisitionDevice<?> acquisitionDevice,
      @Reference ProcessingService processingService) {
    this.acquisitionDevice = acquisitionDevice;
    this.sampleResource = Requirement.on(stageExperiment.samplePreparation());
    this.processingService = processingService;
  }

  @Override
  public Unit<Mass> getMassUnit() {
    return dalton().getUnit();
  }

  @Override
  public AcquisitionDevice<?> getAcquisitionDevice() {
    return acquisitionDevice;
  }

  @Override
  public ConditionRequirement<Void> sampleResource() {
    return sampleResource;
  }

  @Override
  public ProcessingService processingService() {
    return processingService;
  }
}
