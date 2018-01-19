package uk.co.saiman.saint.impl;

import java.util.List;
import java.util.Random;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.processing.ProcessorState;
import uk.co.saiman.saint.SaintSpectrumConfiguration;

final class SaintSpectrumConfigurationImpl implements SaintSpectrumConfiguration {
  private String name;
  private final ConfigurationContext<SaintSpectrumConfiguration> context;
  private final AcquisitionDevice acquisitionDevice;
  private final List<ProcessorState> processors;

  SaintSpectrumConfigurationImpl(
      SaintSpectrumExperimentType experimentType,
      ConfigurationContext<SaintSpectrumConfiguration> context) {
    this.context = context;
    this.acquisitionDevice = experimentType.getAcquisitionDevice();
    this.processors = experimentType.createProcessorList(context.persistedState());
    name = context.getId(() -> "test-" + new Random().nextInt(Integer.MAX_VALUE));
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
  public List<ProcessorState> getProcessing() {
    return processors;
  }

  @Override
  public void setProcessing(List<DataProcessor> processors) {
    processors.clear();
    processors.addAll(processors);
  }
}