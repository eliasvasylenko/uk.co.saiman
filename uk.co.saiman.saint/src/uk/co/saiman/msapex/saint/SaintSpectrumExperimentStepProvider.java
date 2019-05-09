package uk.co.saiman.msapex.saint;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Condition;
import uk.co.saiman.msapex.experiment.step.provider.DefineStep;
import uk.co.saiman.msapex.experiment.step.provider.ExperimentStepProvider;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.SaintSpectrumExecutor;

@Component
public class SaintSpectrumExperimentStepProvider
    implements ExperimentStepProvider<Condition<Void>> {
  private final SaintProperties properties;
  private final SaintSpectrumExecutor spectrumExecutor;

  @Activate
  public SaintSpectrumExperimentStepProvider(
      @Reference PropertyLoader properties,
      @Reference SaintSpectrumExecutor spectrumExecutor) {
    this.properties = properties.getProperties(SaintProperties.class);
    this.spectrumExecutor = spectrumExecutor;
  }

  @Override
  public Localized<String> name() {
    return properties.stageExperimentStepName();
  }

  @Override
  public Executor<Condition<Void>> executor() {
    return spectrumExecutor;
  }

  @Override
  public Stream<StepDefinition<Condition<Void>>> createSteps(
      DefineStep<Condition<Void>> defineStep) {
    return Stream.of(defineStep.withName("Spectrum"));
  }
}
