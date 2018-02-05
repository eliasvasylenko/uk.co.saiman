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

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.instrument.stage.XYStageDevice;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.property.Property;
import uk.co.saiman.saint.SaintXYStageConfiguration;

@Component
public class SaintXYStageExperimentType implements XYStageExperimentType<SaintXYStageConfiguration>,
    ExperimentType<SaintXYStageConfiguration, Void> {
  private static final String X_STATE = "xOffset";
  private static final String Y_STATE = "yOffset";

  @Reference
  XYStageDevice stageDevice;

  @Reference
  Units units;

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public SaintXYStageConfiguration createState(
      ConfigurationContext<SaintXYStageConfiguration> context) {
    String id = context.getId(() -> "A1");

    return new SaintXYStageConfiguration() {
      private final Property<Quantity<Length>> x = getLength(X_STATE);
      private final Property<Quantity<Length>> y = getLength(Y_STATE);

      private Property<Quantity<Length>> getLength(String value) {
        return context
            .persistedState()
            .forString(value)
            .map(l -> units.parseQuantity(l).asType(Length.class), units::formatQuantity)
            .setDefault(() -> units.metre().micro().getQuantity(0));
      }

      @Override
      public String getName() {
        return id;
      }

      @Override
      public XYStageDevice stageDevice() {
        return stageDevice;
      }

      @Override
      public void setX(Quantity<Length> offset) {
        x.set(offset);
      }

      @Override
      public void setY(Quantity<Length> offset) {
        y.set(offset);
      }

      @Override
      public Quantity<Length> getX() {
        return x.get();
      }

      @Override
      public Quantity<Length> getY() {
        return y.get();
      }

      @Override
      public String toString() {
        return "(" + units.formatQuantity(getX()) + ", " + units.formatQuantity(getY()) + ")";
      }
    };
  }

  @Override
  public XYStageDevice device() {
    return stageDevice;
  }
}
