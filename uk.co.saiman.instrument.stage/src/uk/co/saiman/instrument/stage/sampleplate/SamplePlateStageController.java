/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.stage.
 *
 * uk.co.saiman.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.stage.sampleplate;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.experiment.sampleplate.SampleArea;
import uk.co.saiman.experiment.sampleplate.SamplePreparation;
import uk.co.saiman.instrument.DeviceImpl.ControlContext;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.StageController;

public class SamplePlateStageController<T extends SamplePreparation>
    implements StageController<SampleArea> {
  private final SamplePlateStage<T, ?> samplePlateStage;

  private SampleArea request;

  private final ControlContext context;

  public SamplePlateStageController(
      SamplePlateStage<T, ?> samplePlateStage,
      ControlContext context) {
    this.samplePlateStage = samplePlateStage;
    this.context = context;
  }

  @Override
  public void withdrawRequest() {
    try (var lock = context.acquireLock()) {
      this.samplePlateStage.underlyingController().withdrawRequest();
    }
  }

  @Override
  public void requestExchange() {
    try (var lock = context.acquireLock()) {
      this.samplePlateStage.underlyingController().requestExchange();
    }
  }

  @Override
  public void requestReady() {
    try (var lock = context.acquireLock()) {
      this.samplePlateStage.underlyingController().requestReady();
    }
  }

  @Override
  public void requestAnalysis(SampleArea position) {
    try (var lock = context.acquireLock()) {
      this.request = position;
      this.samplePlateStage.underlyingController().requestAnalysis(position.rest());
    }
  }

  @Override
  public SampleState<SampleArea> awaitRequest(long timeout, TimeUnit unit) {
    try (var lock = context.acquireLock()) {
      var state = this.samplePlateStage.underlyingController().awaitRequest(timeout, unit);
      return SampleState.map(state, t -> request);
    }
  }

  @Override
  public SampleState<SampleArea> awaitReady(long timeout, TimeUnit unit) {
    try (var lock = context.acquireLock()) {
      var state = this.samplePlateStage.underlyingController().awaitReady(timeout, unit);
      return SampleState.map(state, t -> null);
    }
  }

  @Override
  public void close() {
    context.close();
  }

  @Override
  public boolean isOpen() {
    return context.isOpen();
  }

  public boolean expectSamplePreparation(T samplePreparation) {
    try (var lock = context.acquireLock()) {
      return samplePlateStage.expectSamplePreparation(samplePreparation);
    }
  }

  public void clearExpectedSamplePreparation() {
    try (var lock = context.acquireLock()) {
      samplePlateStage.clearExpectedSamplePreparation();
    }
  }

  public void clearAssumedSamplePreparation() {
    try (var lock = context.acquireLock()) {
      samplePlateStage.clearAssumedSamplePreparation();
    }
  }
}