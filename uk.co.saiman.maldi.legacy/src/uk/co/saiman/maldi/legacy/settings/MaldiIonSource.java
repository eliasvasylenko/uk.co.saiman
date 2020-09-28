package uk.co.saiman.maldi.legacy.settings;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = MaldiIonSource.Settings.class, factory = true)
@Component(service = MaldiIonSource.class, configurationPid = MaldiIonSource.CONFIGURATION_PID, configurationPolicy = REQUIRE, immediate = true)
public class MaldiIonSource {
  @ObjectClassDefinition(name = "Legacy MALDI ToF Analyser", description = "Experiment settings for MALDI ToF acquisition")
  public @interface Settings {
    @AttributeDefinition(name = "Sample Matrix", description = "[Placeholder for real settings]")
    String matrix();

    @AttributeDefinition(name = "Laser config", description = "[Placeholder for real settings]")
    String laser();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.legacy.ionsource";

  private final String matrix;
  private final String laser;

  @Activate
  public MaldiIonSource(Settings settings) {
    matrix = settings.matrix();
    laser = settings.laser();
  }

  public String matrix() {
    return matrix;
  }

  public String laser() {
    return laser;
  }
}
