package uk.co.saiman.maldi.legacy.settings;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.maldi.legacy.queue.LegacyQueueImportException;

@Component(service = MaldiLegacySettings.class)
public class MaldiLegacySettings {
  private final Map<String, MaldiIonSource> ionSources;
  private final Map<String, MaldiTofAnalyser> tofAnalysers;
  private final Map<String, MaldiOptimisation> optimisations;
  private final Map<String, MaldiInternalCalibration> internalCalibrations;
  private final MaldiPeakDetection peakDetection;
  private final MaldiProcessing processing;

  @Activate
  public MaldiLegacySettings(
      @Reference List<MaldiIonSource> ionSources,
      @Reference List<MaldiTofAnalyser> tofAnalysers,
      @Reference List<MaldiOptimisation> optimisations,
      @Reference List<MaldiInternalCalibration> internalCalibrations,
      @Reference MaldiPeakDetection peakDetection,
      @Reference MaldiProcessing processing) {
    this.ionSources = ionSources.stream().collect(toMap(MaldiIonSource::id, identity(), (a, b) -> a));
    this.tofAnalysers = tofAnalysers.stream().collect(toMap(MaldiTofAnalyser::id, identity(), (a, b) -> a));
    this.optimisations = optimisations.stream().collect(toMap(MaldiOptimisation::id, identity(), (a, b) -> a));
    this.internalCalibrations = internalCalibrations
        .stream()
        .collect(toMap(MaldiInternalCalibration::id, identity(), (a, b) -> a));
    this.peakDetection = peakDetection;
    this.processing = processing;
    // TODO Auto-generated constructor stub
  }

  private <T> T require(T item, String string, String id) {
    try {
      return Objects.requireNonNull(item);
    } catch (Exception e) {
      throw new LegacyQueueImportException("Failed to locate " + string + " settings with id '" + id + "'", e);
    }
  }

  public MaldiIonSource lookupIonSource(String id) {
    return require(ionSources.get(id), "ion source", id);
  }

  public MaldiTofAnalyser lookupTofAnalyser(String id) {
    return require(tofAnalysers.get(id), "ToF analyser", id);
  }

  public MaldiOptimisation lookupOptimisation(String id) {
    return require(optimisations.get(id), "optimisation", id);
  }

  public MaldiInternalCalibration lookupInternalCalibration(String id) {
    return require(internalCalibrations.get(id), "internal calibration", id);
  }

  public ExperimentStepDesign lookupPeakDetection(String id) {
    return peakDetection.findMethod(id);
  }

  public ExperimentStepDesign lookupProcessing(String id) {
    return processing.findMethod(id);
  }
}
