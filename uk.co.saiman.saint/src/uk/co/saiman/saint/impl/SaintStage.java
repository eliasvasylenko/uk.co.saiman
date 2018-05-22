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

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import javax.measure.quantity.Length;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.instrument.ConnectionState;
import uk.co.saiman.instrument.Instrument;
import uk.co.saiman.instrument.sample.SampleDevice;
import uk.co.saiman.instrument.sample.SampleState;
import uk.co.saiman.instrument.stage.Stage;
import uk.co.saiman.instrument.stage.XYStage;
import uk.co.saiman.instrument.stage.composed.ComposedXYStage;
import uk.co.saiman.instrument.stage.composed.StageAxis;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.observable.ObservableValue;

@Designate(ocd = SaintStage.SaintStageConfiguration.class, factory = true)
@Component(
    name = SaintStage.CONFIGURATION_PID,
    configurationPid = SaintStage.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class SaintStage
    implements SampleDevice<XYCoordinate<Length>>, Stage<XYCoordinate<Length>>, XYStage {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.saint";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "SAINT Stage Configuration",
      description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface SaintStageConfiguration {
    String name();

    String lowerBound();

    String upperBound();

    String exchangeLocation();

    String analysisLocation();
  }

  @Reference
  private Instrument instrument;

  @Reference
  private StageAxis<Length> xAxis;

  @Reference
  private StageAxis<Length> yAxis;

  private ComposedXYStage stage;

  @Activate
  void activate(SaintStageConfiguration configuration) {
    this.stage = new ComposedXYStage(
        configuration.name(),
        instrument,
        xAxis,
        yAxis,
        XYCoordinate.fromString(configuration.lowerBound()).asType(Length.class),
        XYCoordinate.fromString(configuration.upperBound()).asType(Length.class),
        XYCoordinate.fromString(configuration.analysisLocation()).asType(Length.class),
        XYCoordinate.fromString(configuration.exchangeLocation()).asType(Length.class));
  }

  @Override
  public String getName() {
    return stage.getName();
  }

  @Override
  public Instrument getInstrument() {
    return stage.getInstrument();
  }

  @Override
  public ObservableValue<ConnectionState> connectionState() {
    return stage.connectionState();
  }

  @Override
  public XYCoordinate<Length> getLowerBound() {
    return stage.getLowerBound();
  }

  @Override
  public XYCoordinate<Length> getUpperBound() {
    return stage.getUpperBound();
  }

  @Override
  public void abortRequest() {
    stage.abortRequest();
  }

  @Override
  public ObservableValue<SampleState> sampleState() {
    return stage.sampleState();
  }

  @Override
  public boolean isLocationReachable(XYCoordinate<Length> location) {
    return stage.isLocationReachable(location);
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> requestedLocation() {
    return stage.requestedLocation();
  }

  @Override
  public ObservableValue<XYCoordinate<Length>> actualLocation() {
    return stage.actualLocation();
  }

  @Override
  public SampleState requestExchange() {
    return stage.requestExchange();
  }

  @Override
  public SampleState requestAnalysis() {
    return stage.requestAnalysis();
  }

  @Override
  public SampleState requestAnalysisLocation(XYCoordinate<Length> location) {
    return stage.requestAnalysisLocation(location);
  }
}
