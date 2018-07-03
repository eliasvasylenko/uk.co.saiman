package uk.co.saiman.acquisition.adq;

import static uk.co.saiman.acquisition.adq.AdqProductId.ADQ114;

public interface Adq114Device extends AdqDevice {
  @Override
  default AdqProductId getProductId() {
    return ADQ114;
  }
}
