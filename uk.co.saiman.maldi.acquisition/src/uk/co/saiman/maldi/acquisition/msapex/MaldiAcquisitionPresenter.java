package uk.co.saiman.maldi.acquisition.msapex;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.msapex.environment.ResourcePresenter;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants;

@Component(enabled = true, immediate = true)
public class MaldiAcquisitionPresenter implements ResourcePresenter<AcquisitionDevice<?>> {
  @Override
  public String getLocalizedLabel() {
    return "Maldi Acquisition Device";
  }

  @Override
  public String getIconURI() {
    return "fugue:size16/system-monitor.png";
  }

  @Override
  public Class<? super AcquisitionDevice<?>> getResourceClass() {
    return AcquisitionDevice.class;
  }

  @Override
  public Provision<AcquisitionDevice<?>> getProvision() {
    return MaldiAcquisitionConstants.MALDI_ACQUISITION_DEVICE;
  }
}
