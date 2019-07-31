package uk.co.saiman.maldi.acquisition;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants.MALDI_ACQUISITION_CONTROLLER;
import static uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants.MALDI_ACQUISITION_DEVICE;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.experiment.dependency.Resource;
import uk.co.saiman.experiment.dependency.source.Provision;
import uk.co.saiman.experiment.environment.ResourceMissingException;
import uk.co.saiman.experiment.environment.ResourceUnavailableException;
import uk.co.saiman.experiment.service.LimitedResourceProvider;
import uk.co.saiman.experiment.service.UnlimitedResourceProvider;
import uk.co.saiman.instrument.acquisition.AcquisitionDevice;
import uk.co.saiman.maldi.acquisition.MaldiAcquisitionProvider.MaldiAcquisitionConfiguration;

@Designate(ocd = MaldiAcquisitionConfiguration.class, factory = true)
@Component(configurationPid = MaldiAcquisitionProvider.CONFIGURATION_PID, configurationPolicy = REQUIRE, enabled = true, immediate = true, service = {
    UnlimitedResourceProvider.class,
    LimitedResourceProvider.class })
public class MaldiAcquisitionProvider
    implements UnlimitedResourceProvider, LimitedResourceProvider {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(name = "Acquisition Configuration", description = "A service to provide an acquisition device within a Maldi environment")
  public @interface MaldiAcquisitionConfiguration {}

  static final String CONFIGURATION_PID = "uk.co.saiman.maldi.acquisition.resource";

  private final AcquisitionDevice<?> device;

  @Activate
  public MaldiAcquisitionProvider(@Reference(name = "device") AcquisitionDevice<?> device) {
    this.device = device;
  }

  @Override
  public Stream<? extends Provision<?>> limitedProvisions() {
    return Stream.of(MALDI_ACQUISITION_CONTROLLER);
  }

  @Override
  public <T> Resource<T> provideResource(Provision<T> provision, long timeout, TimeUnit unit) {
    if (provision != MALDI_ACQUISITION_CONTROLLER) {
      throw new ResourceMissingException(provision);
    }

    try {
      var controller = device.acquireControl(timeout, unit);
      return new Resource<T>() {
        @Override
        public Provision<T> source() {
          return provision;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T value() {
          return (T) controller;
        }

        @Override
        public void close() {
          controller.close();
        }
      };
    } catch (TimeoutException | InterruptedException e) {
      throw new ResourceUnavailableException(provision, e);
    }
  }

  @Override
  public Stream<? extends Provision<?>> unlimitedProvisions() {
    return Stream.of(MALDI_ACQUISITION_DEVICE);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T provideValue(Provision<T> provision) {
    if (provision != MALDI_ACQUISITION_DEVICE) {
      throw new ResourceMissingException(provision);
    }
    return (T) device;
  }
}
