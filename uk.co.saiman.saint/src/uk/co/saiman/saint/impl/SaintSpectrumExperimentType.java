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

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;
import static uk.co.saiman.experiment.spectrum.SpectrumProcessorState.PROCESSING_KEY;
import static uk.co.saiman.experiment.spectrum.SpectrumProcessorState.PROCESSOR_TYPE_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentNodeConstraint;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.experiment.spectrum.MissingSpectrumProcessorType;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumProcessorState;
import uk.co.saiman.experiment.spectrum.SpectrumProcessorType;
import uk.co.saiman.experiment.spectrum.SpectrumProperties;
import uk.co.saiman.saint.SaintSpectrumConfiguration;
import uk.co.saiman.saint.SaintXYStageConfiguration;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class SaintSpectrumExperimentType extends SpectrumExperimentType<SaintSpectrumConfiguration>
    implements ExperimentType<SaintSpectrumConfiguration, Spectrum> {
  @Reference
  XYStageExperimentType<SaintXYStageConfiguration> stageExperiment;

  @Reference
  AcquisitionDevice acquisitionDevice;

  private final Map<String, SpectrumProcessorType<?>> processingTypes = new HashMap<>();

  @Reference
  PropertyLoader propertyLoader;

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC)
  void addProcessingType(SpectrumProcessorType<?> type) {
    processingTypes.putIfAbsent(type.getId(), type);
  }

  void removeProcessingType(SpectrumProcessorType<?> type) {
    processingTypes.remove(type.getId());
  }

  @Override
  public String getId() {
    return getClass().getName();
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
    return UNFULFILLED;
  }

  @Override
  protected AcquisitionDevice getAcquisitionDevice() {
    return acquisitionDevice;
  }

  protected List<SpectrumProcessorState> createProcessorList(PersistedState persistedState) {
    return persistedState
        .getMapList(PROCESSING_KEY)
        .map(this::createProcessorConfiguration, SpectrumProcessorState::getPersistedState);
  }

  protected SpectrumProcessorState createProcessorConfiguration(PersistedState persistedState) {
    return processingTypes
        .computeIfAbsent(
            persistedState.forString(PROCESSOR_TYPE_KEY).get(),
            id -> new MissingSpectrumProcessorType(
                id,
                propertyLoader.getProperties(SpectrumProperties.class)))
        .configure(persistedState);
  }
}
