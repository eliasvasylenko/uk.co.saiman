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

import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.FULFILLED;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.UNFULFILLED;

import java.util.AbstractList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.spectrum.Spectrum;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentNodeConstraint;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.persistence.PersistedStateList;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumProcessorType;
import uk.co.saiman.saint.SaintSpectrumConfiguration;
import uk.co.saiman.saint.SaintXYStageConfiguration;

@Component
public class SaintSpectrumExperimentType extends SpectrumExperimentType<SaintSpectrumConfiguration>
    implements ExperimentType<SaintSpectrumConfiguration, Spectrum> {
  private static final String PROCESSING_KEY = "processing";
  private static final String PROCESSOR_TYPE_KEY = "type";

  @Reference
  XYStageExperimentType<SaintXYStageConfiguration> stageExperiment;

  @Reference
  AcquisitionDevice acquisitionDevice;

  @Reference(policy = DYNAMIC)
  List<SpectrumProcessorType> processingTypes = new CopyOnWriteArrayList<>();

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

  protected List<SpectrumProcessorType> createProcessorList(PersistedState persistedState) {
    PersistedStateList processingList = persistedState.forMapList(PROCESSING_KEY);

    return new AbstractList<SpectrumProcessorType>() {
      @Override
      public SpectrumProcessorType get(int index) {
        PersistedState processorState = processingList.get(index);
        return processingTypes
            .stream()
            .filter(p -> p.getId().equals(processorState.forString(PROCESSOR_TYPE_KEY).get()))
            .findFirst()
            .get() // TODO orElseThrow something sensible
            .load(processorState);
      }

      @Override
      public int size() {
        return processingList.size();
      }
    };
  }
}
