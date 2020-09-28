package uk.co.saiman.maldi.legacy.settings;

public interface MaldiLegacySettings {
  MaldiIonSource lookupIonSource(String id);

  MaldiTofAnalyser lookupTofAnalyser(String id);

  MaldiOptimisation lookupOptimisation(String id);

  MaldiInternalCalibration lookupInternalCalibration(String textContent);

  MaldiPeakDetection lookupPeakDetection(String textContent);

  MaldiProcessing lookupProcessing(String textContent);
}
