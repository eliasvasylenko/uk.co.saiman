package uk.co.saiman.experiment.spectrum;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.text.properties.PropertyLoader;

@Component
public class ConvolutionSpectrumProcessor
    implements SpectrumProcessorType<ConvolutionSpectrumProcessorConfiguration> {
  @Reference
  PropertyLoader propertyLoader;
  private SpectrumProperties properties;

  public ConvolutionSpectrumProcessor(SpectrumProperties properties) {
    this.properties = properties;
  }

  protected ConvolutionSpectrumProcessor() {}

  @Activate
  void activate() {
    properties = propertyLoader.getProperties(SpectrumProperties.class);
  }

  @Override
  public String getId() {
    return getClass().getSimpleName();
  }

  @Override
  public String getName() {
    return properties.convolutionProcessor().get();
  }

  @Override
  public String getDescription() {
    return properties.convolutionProcessorDescription().get();
  }

  @Override
  public ConvolutionSpectrumProcessorConfiguration createConfiguration(PersistedState state) {
    return new ConvolutionSpectrumProcessorConfiguration(state);
  }
}
