package uk.co.saiman.maldi.legacy.settings;

import static org.osgi.service.component.annotations.ConfigurationPolicy.OPTIONAL;

import java.nio.file.Path;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.data.Data;
import uk.co.saiman.data.resource.PathLocation;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.design.json.JsonExperimentStepDesignFormat;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.maldi.legacy.queue.LegacyQueueImportException;

@Designate(ocd = MaldiPeakDetection.Settings.class)
@Component(service = MaldiPeakDetection.class, configurationPid = MaldiPeakDetection.CONFIGURATION_PID, configurationPolicy = OPTIONAL, immediate = true)
public class MaldiPeakDetection {
  @ObjectClassDefinition(name = "Legacy MALDI Peak Detection", description = "Configuration for peak detection of spectra")
  public @interface Settings {
    @AttributeDefinition(name = "Experiment Method JSON Directory", description = "Directory containing JSON format experiment step design files for peak detection")
    String methodDirectory();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.legacy.peakdetection";

  private final JsonExperimentStepDesignFormat format;
  private final Path methodDirectory;

  @Activate
  public MaldiPeakDetection(Settings settings, @Reference ExecutorService executors) {
    this.format = new JsonExperimentStepDesignFormat(executors);
    this.methodDirectory = Path.of(settings.methodDirectory());
  }

  public ExperimentStepDesign findMethod(String name) {
    try {
      var location = new PathLocation(methodDirectory);
      var method = Data.locate(location, name, format).get();
      return method;
    } catch (Exception e) {
      throw new LegacyQueueImportException(
          "Failed to read peak detection settings from directory " + methodDirectory,
          e);
    }
  }
}
