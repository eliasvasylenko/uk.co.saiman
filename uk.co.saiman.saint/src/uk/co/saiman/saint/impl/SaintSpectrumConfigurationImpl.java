package uk.co.saiman.saint.impl;

import java.util.List;
import java.util.Random;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.spectrum.SpectrumProcessorState;
import uk.co.saiman.saint.SaintSpectrumConfiguration;

final class SaintSpectrumConfigurationImpl implements SaintSpectrumConfiguration {
  private String name;
  private final ConfigurationContext<SaintSpectrumConfiguration> context;
  private final AcquisitionDevice acquisitionDevice;
  private final List<SpectrumProcessorState> processors;

  SaintSpectrumConfigurationImpl(
      SaintSpectrumExperimentType experimentType,
      ConfigurationContext<SaintSpectrumConfiguration> context) {
    this.context = context;
    this.acquisitionDevice = experimentType.acquisitionDevice;
    this.processors = experimentType.createProcessorList(context.persistedState());
    name = context.getId(() -> "test-" + new Random().nextInt(Integer.MAX_VALUE));

    // context.persistedState().forMapList(PROCESSORS).stream().map(arg0);
  }

  @Override
  public void setSpectrumName(String name) {
    this.name = name;
    context.setId(name);
  }

  @Override
  public String getSpectrumName() {
    return name;
  }

  @Override
  public AcquisitionDevice getAcquisitionDevice() {
    return acquisitionDevice;
  }

  @Override
  public List<SpectrumProcessorState> getProcessing() {
    return processors;
  }

  @Override
  public void setProcessing(List<SpectrumProcessor> processors) {
    processors.clear();
    processors.addAll(processors);
  }
}