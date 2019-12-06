package uk.co.saiman.instrument.acquisition.adq.impl;

public interface AdqAcquisition extends AutoCloseable {
  public void acquire();
}
