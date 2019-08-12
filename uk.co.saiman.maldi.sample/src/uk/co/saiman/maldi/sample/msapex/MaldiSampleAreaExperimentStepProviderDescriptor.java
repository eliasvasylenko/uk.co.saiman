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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.sample.msapex;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.msapex.step.provider.StepProvider;
import uk.co.saiman.experiment.msapex.step.provider.StepProviderDescriptor;
import uk.co.saiman.maldi.stage.i18n.MaldiStageProperties;

@Component
public class MaldiSampleAreaExperimentStepProviderDescriptor implements StepProviderDescriptor {
  public static final String ID = "uk.co.saiman.experiment.step.provider.xysample.maldi";

  private final MaldiStageProperties properties;

  @Activate
  public MaldiSampleAreaExperimentStepProviderDescriptor(
      @Reference MaldiStageProperties properties) {
    this.properties = properties;
  }

  @Override
  public String getLabel() {
    return properties.samplePlateExperimentStepName().toString();
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getIconURI() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Class<? extends StepProvider> getProviderClass() {
    return MaldiSampleAreaExperimentStepProvider.class;
  }
}