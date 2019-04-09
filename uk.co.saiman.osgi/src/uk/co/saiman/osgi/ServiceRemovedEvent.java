package uk.co.saiman.osgi;

import org.osgi.framework.ServiceReference;

public class ServiceRemovedEvent extends ServiveEvent {
  private final ServiceReference<?> reference;

  public ServiceRemovedEvent(ServiceReference<?> reference) {
    this.reference = reference;
  }

  @Override
  public ServiceReference<?> reference() {
    return reference;
  }
}
