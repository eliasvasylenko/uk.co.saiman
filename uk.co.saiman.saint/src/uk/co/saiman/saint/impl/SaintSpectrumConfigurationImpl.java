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

import java.util.List;
import java.util.Random;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.processing.ProcessorState;
import uk.co.saiman.saint.SaintSpectrumConfiguration;

final class SaintSpectrumConfigurationImpl implements SaintSpectrumConfiguration {
  private String name;
  private final ConfigurationContext<SaintSpectrumConfiguration> context;
  private final AcquisitionDevice acquisitionDevice;
  private final List<ProcessorState> processors;

  SaintSpectrumConfigurationImpl(
      SaintSpectrumExperimentType experimentType,
      ConfigurationContext<SaintSpectrumConfiguration> context) {
    this.context = context;
    this.acquisitionDevice = experimentType.getAcquisitionDevice();
    this.processors = experimentType.createProcessorList(context.persistedState());
    name = context.getId(() -> "test-" + new Random().nextInt(Integer.MAX_VALUE));
  }

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
  public List<ProcessorState> getProcessing() {
    return processors;
  }
}