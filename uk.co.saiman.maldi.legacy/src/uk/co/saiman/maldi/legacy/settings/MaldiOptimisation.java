package uk.co.saiman.maldi.legacy.settings;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.spectrum.SelectiveAccumulation;

@Designate(ocd = MaldiOptimisation.Settings.class, factory = true)
@Component(service = MaldiOptimisation.class, configurationPid = MaldiOptimisation.CONFIGURATION_PID, configurationPolicy = REQUIRE, immediate = true)
public class MaldiOptimisation {
  @ObjectClassDefinition(name = "Legacy MALDI Optimisation", description = "Experiment settings for dynamic signal optimisation")
  public @interface Settings {
    @AttributeDefinition(name = "ID", description = "Unique identifier of the configuration")
    String id();

    /*
     * Selective acquisition settings
     */
    @AttributeDefinition(name = "Selective Accumulation", description = "Accumulate spectra selectively as a function of signal quality, according to the named strategy/")
    String selectiveAccumulationStrategy();

    /*
     * Laser optimisation settings
     */

    LaserPowerOptimisation.Guidance laserPowerOptimisationGuidance();

    String initialLaserPower();

    String laserThresholdAtSignalLow();

    String laserThresholdAtSignalHigh();

    String laserThresholdMaximum();

    String laserPowerIncrementSteps();

    boolean doNotDecrementLaserWhilstSignalGood();

    @AttributeDefinition(name = "Laser Power Optimisation Window", description = "Window size to consider signal quality for laser power optimisation.")
    int laserPowerOptimisationWindow();

    boolean onSignalSeenDoNotAdjustLaser();

    /*
     * Sample position optimisation settings.
     */

    SamplePositionOptimisation.Guidance samplePositionOptimisationGuidance();

    String samplePositionOptimisationPattern();

    @AttributeDefinition(name = "Sample Position Optimisation Window", description = "Window size to consider signal quality for sample position optimisation.")
    int samplePositionOptimisationWindow();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.legacy.optimisation";

  private final String id;
  private LaserPowerOptimisation laserPowerOptimisation;
  private SamplePositionOptimisation samplePositionOptimisation; // not valid for imaging experiments
  private SelectiveAccumulation selectiveAccumulation;

  @Activate
  public MaldiOptimisation(Settings settings) {
    id = settings.id();
  }

  public String id() {
    return id;
  }

  public LaserPowerOptimisation laserPowerOptimisation() {
    return laserPowerOptimisation;
  }

  public SamplePositionOptimisation samplePositionOptimisation() {
    return samplePositionOptimisation;
  }

  public SelectiveAccumulation selectiveAccumulation() {
    return selectiveAccumulation;
  }
}
