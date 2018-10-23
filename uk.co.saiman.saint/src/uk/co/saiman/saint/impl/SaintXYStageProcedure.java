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
import static uk.co.saiman.experiment.state.Accessor.stringAccessor;
import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.metre;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.ExperimentContext;
import uk.co.saiman.experiment.Procedure;
import uk.co.saiman.experiment.sample.XYStageProcedure;
import uk.co.saiman.experiment.state.Accessor.PropertyAccessor;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.measurement.scalar.Scalar;
import uk.co.saiman.saint.SaintXYStageConfiguration;

@Component
public class SaintXYStageProcedure implements XYStageProcedure<SaintXYStageConfiguration>,
    Procedure<SaintXYStageConfiguration, Void> {
  private static final PropertyAccessor<Quantity<Length>> X = lengthAccessor("xOffset");
  private static final PropertyAccessor<Quantity<Length>> Y = lengthAccessor("yOffset");

  private static PropertyAccessor<Quantity<Length>> lengthAccessor(String value) {
    return stringAccessor(value)
        .map(l -> quantityFormat().parse(l).asType(Length.class), quantityFormat()::format);
  }

  @Reference(cardinality = OPTIONAL)
  private XYStage stageDevice;

  @Override
  public String getId() {
    return SaintXYStageProcedure.class.getName();
  }

  @Override
  public SaintXYStageConfiguration configureVariables(
      ExperimentContext<SaintXYStageConfiguration> context) {
    String id = context.getId(() -> "A1");

    return new SaintXYStageConfiguration() {
      {
        context
            .update(
                state -> state
                    .withDefault(X, () -> new Scalar<>(metre().micro(), 0))
                    .withDefault(Y, () -> new Scalar<>(metre().micro(), 0)));
      }

      @Override
      public String getName() {
        return id;
      }

      @Override
      public XYStage stageDevice() {
        return stageDevice;
      }

      @Override
      public XYCoordinate<Length> location() {
        return new XYCoordinate<>(context.stateMap().get(X), context.stateMap().get(Y));
      }

      @Override
      public void setLocation(XYCoordinate<Length> location) {
        context.update(state -> state.with(X, location.getX()).with(Y, location.getY()));
      }

      @Override
      public String toString() {
        return "("
            + quantityFormat().format(location().getX())
            + ", "
            + quantityFormat().format(location().getY())
            + ")";
      }
    };
  }

  @Override
  public XYStage device() {
    return stageDevice;
  }
}
