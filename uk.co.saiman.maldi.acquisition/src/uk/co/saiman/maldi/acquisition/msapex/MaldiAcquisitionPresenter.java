package uk.co.saiman.maldi.acquisition.msapex;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.msapex.environment.ResourcePresenter;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;

@SuppressWarnings("rawtypes")
@Component(enabled = true, immediate = true)
public class MaldiAcquisitionPresenter implements ResourcePresenter<AcquisitionDevice> {
  @Override
  public String getLocalizedLabel() {
    return "Maldi Acquisition Device";
  }

  @Override
  public String getIconURI() {
    return "fugue:size16/system-monitor.png";
  }

  @Override
  public Class<AcquisitionDevice> getResourceClass() {
    return AcquisitionDevice.class;
  }
}
