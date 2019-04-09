package uk.co.saiman.osgi;

import org.osgi.framework.ServiceReference;

public abstract class ServiveEvent {
  public abstract ServiceReference<?> reference();
}
