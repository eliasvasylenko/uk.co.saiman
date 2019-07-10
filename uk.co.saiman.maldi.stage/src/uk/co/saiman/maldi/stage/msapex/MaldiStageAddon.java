package uk.co.saiman.maldi.stage.msapex;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.maldi.stage.SampleAreaStage;
import uk.co.saiman.maldi.stage.SamplePlateStage;

public class MaldiStageAddon {
  @Inject
  @Service
  private SampleAreaStage sampleAreaStage;

  @Inject
  private IEclipseContext context;

  @PostConstruct
  void initialize() {
    context.set(SampleAreaStage.class, sampleAreaStage);
    context.set(SamplePlateStage.class, sampleAreaStage.samplePlateStage());
  }
}
