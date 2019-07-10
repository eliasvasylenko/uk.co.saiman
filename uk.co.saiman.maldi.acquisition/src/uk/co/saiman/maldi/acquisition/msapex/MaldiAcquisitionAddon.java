package uk.co.saiman.maldi.acquisition.msapex;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.maldi.acquisition.MaldiAcquisitionConstants;
import uk.co.saiman.maldi.stage.SampleAreaStage;

public class MaldiAcquisitionAddon {
  @Inject
  private SampleAreaStage sampleAreaStage;

  @Inject
  private IEclipseContext context;

  @PostConstruct
  void initialize() {}
}
