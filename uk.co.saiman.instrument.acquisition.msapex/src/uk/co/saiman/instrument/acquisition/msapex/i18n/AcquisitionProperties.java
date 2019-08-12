package uk.co.saiman.instrument.acquisition.msapex.i18n;

import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.service.PropertiesService;

@PropertiesService
public interface AcquisitionProperties {
  Localized<String> acquisitionDevice();
}
