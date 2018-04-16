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
import static uk.co.saiman.experiment.ExperimentNodeConstraint.FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.VIOLATED;
import static uk.co.saiman.experiment.processing.ProcessorState.PROCESSING_KEY;
import static uk.co.saiman.measurement.Units.dalton;

import java.util.List;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentNodeConstraint;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ProcessingType;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.processing.ProcessorService;
import uk.co.saiman.experiment.processing.ProcessorState;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumProperties;
import uk.co.saiman.saint.SaintSpectrumConfiguration;
import uk.co.saiman.saint.SaintXYStageConfiguration;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class SaintSpectrumExperimentType extends SpectrumExperimentType<SaintSpectrumConfiguration>
    implements ExperimentType<SaintSpectrumConfiguration, Spectrum> {
  @Reference
  private XYStageExperimentType<SaintXYStageConfiguration> stageExperiment;

  @Reference(cardinality = OPTIONAL)
  private AcquisitionDevice acquisitionDevice;

  @Reference
  private ProcessorService processors;

  @Reference
  private PropertyLoader properties;

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  protected SpectrumProperties getProperties() {
    return properties.getProperties(SpectrumProperties.class);
  }

  @Override
  protected Unit<Mass> getMassUnit() {
    return dalton().getUnit();
  }

  @Override
  public SaintSpectrumConfiguration createState(
      ConfigurationContext<SaintSpectrumConfiguration> context) {
    SaintSpectrumConfiguration configuration = new SaintSpectrumConfigurationImpl(this, context);
    return configuration;
  }

  @Override
  public ExperimentNodeConstraint mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return parentNode.findAncestor(stageExperiment).isPresent() ? FULFILLED : UNFULFILLED;
  }

  @Override
  public ExperimentNodeConstraint mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    return descendantNodeType instanceof ProcessingType<?, ?, ?> ? FULFILLED : VIOLATED;
  }

  @Override
  public AcquisitionDevice getAcquisitionDevice() {
    return acquisitionDevice;
  }

  protected List<ProcessorState> createProcessorList(PersistedState persistedState) {
    return persistedState
        .getMapList(PROCESSING_KEY)
        .map(processors::loadProcessorState, ProcessorState::getPersistedState);
  }
}
