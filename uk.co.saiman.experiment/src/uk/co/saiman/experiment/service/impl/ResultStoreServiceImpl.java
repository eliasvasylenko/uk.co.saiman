package uk.co.saiman.experiment.service.impl;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.service.ResultStorageService;
import uk.co.saiman.experiment.service.ResultStorageStrategy;

@Component
public class ResultStoreServiceImpl implements ResultStorageService {
  @Override
  public ResultStorageStrategy getResultStorageStrategy(String id) {
    // TODO Auto-generated method stub
    return null;
  }
}
