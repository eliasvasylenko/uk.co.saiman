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

/**
 * TODO this class should not need to exist. Fold the annotations into
 * {@link CopleyLinearAxis} using constructor injection with R7.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = SaintXYStage.SaintXYStageConfiguration.class, factory = true)
@Component(
    name = SaintXYStage.CONFIGURATION_PID,
    configurationPid = SaintXYStage.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class SaintXYStage
    implements SampleDevice<XYCoordinate<Length>>, Stage<XYCoordinate<Length>>, XYStage {
  static final String CONFIGURATION_PID = "uk.co.saiman.instrument.stage.composed.xy";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      id = CONFIGURATION_PID,
      name = "SAINT Stage Configuration",
      description = "The configuration for a modular stage composed of an x axis and a y axis")
  public @interface SaintXYStageConfiguration {
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
  void activate(SaintXYStageConfiguration configuration) {
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
  public SampleState requestLocation(XYCoordinate<Length> location) {
    return stage.requestLocation(location);
  }
}
