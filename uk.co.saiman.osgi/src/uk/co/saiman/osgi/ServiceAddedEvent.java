package uk.co.saiman.osgi;

import org.osgi.framework.ServiceReference;

public class ServiceAddedEvent extends ServiveEvent {
  private final ServiceRecord<?, ?, ?> record;

  public ServiceAddedEvent(ServiceRecord<?, ?, ?> record) {
    this.record = record;
  }

  public ServiceRecord<?, ?, ?> record() {
    return record;
  }

  @Override
  public ServiceReference<?> reference() {
    return record.serviceReference();
  }
}
