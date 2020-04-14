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
package uk.co.saiman.maldi.stage;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.Device;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.sampleplate.SamplePlateStage;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePreparation;
import uk.co.saiman.maldi.stage.MaldiStage.MaldiStageConfiguration;

/*
 * 
 * 
 * 
 * 
 * TODO
 * 
 * Do we also want to build-in barcode reading? If so, two options for how to do
 * it:
 * 
 * - automatically at end of exchange IFF the EXPECTED plate type has a barcode.
 * 
 * - must be manually requested, performed against the ASSUMED plate type, then
 * invalidates assumption if wrong
 * 
 * TODO Warning dialog pops up if the expected sample plate has a barcode but we
 * can't read it.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
@Designate(ocd = MaldiStageConfiguration.class, factory = true)
@Component(
    configurationPid = MaldiStage.CONFIGURATION_PID,
    configurationPolicy = REQUIRE,
    service = { Device.class, SamplePlateStage.class, Stage.class, MaldiStage.class },
    immediate = true)
public class MaldiStage extends SamplePlateStage<MaldiSamplePreparation, MaldiStageController> {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Maldi Stage")
  public @interface MaldiStageConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.stage";

  @Activate
  public MaldiStage(MaldiStageConfiguration configuration, @Reference(name = "stage") XYStage stage)
      throws ClassCastException, InterruptedException, TimeoutException {
    this(stage);
  }

  public MaldiStage(XYStage stage) throws InterruptedException, TimeoutException {
    super(stage);
  }

  @Override
  protected MaldiStageController createController(
      ControlContext context,
      long timeout,
      TimeUnit unit) throws TimeoutException, InterruptedException {
    return new MaldiStageController(this, context);
  }

  @Override
  public XYStage sampleAreaStage() {
    // TODO Auto-generated method stub
    return null;
  }
}
