package uk.co.saiman.maldi.legacy.settings;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = MaldiInternalCalibration.Settings.class, factory = true)
@Component(service = MaldiInternalCalibration.class, configurationPid = MaldiInternalCalibration.CONFIGURATION_PID, configurationPolicy = REQUIRE, immediate = true)
public class MaldiInternalCalibration {
  @ObjectClassDefinition(name = "Legacy MALDI Internal Calibration", description = "Experiment settings for MALDI internal calibration")
  public @interface Settings {
    @AttributeDefinition(name = "ID", description = "Unique identifier of the configuration")
    String id();
  }

  public @interface TargetSettings {
    String name();

    double mass();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.legacy.internalcalibration";

  private final String id;

  @Activate
  public MaldiInternalCalibration(Settings settings) {
    id = settings.id();
  }

  public String id() {
    return id;
  }

  public static class Target {
    private final String name;
    private final double mass;

    public Target(String name, double mass) {
      this.name = name;
      this.mass = mass;
    }

    public String name() {
      return name;
    }

    public double mass() {
      return mass;
    }
  }
}
