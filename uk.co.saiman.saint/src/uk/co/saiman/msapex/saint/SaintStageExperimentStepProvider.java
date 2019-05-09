package uk.co.saiman.msapex.saint;

import static uk.co.saiman.experiment.sample.XYStageExecutor.LOCATION;

import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.production.Nothing;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.measurement.coordinate.XYCoordinate;
import uk.co.saiman.msapex.experiment.step.provider.DefineStep;
import uk.co.saiman.msapex.experiment.step.provider.ExperimentStepProvider;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.saint.SaintProperties;
import uk.co.saiman.saint.SaintXYStageExecutor;

@Component
public class SaintStageExperimentStepProvider implements ExperimentStepProvider<Nothing> {
  private final SaintProperties properties;
  private final SaintStageDiagram stageDiagram;
  private final SaintXYStageExecutor stageExecutor;

  @Activate
  public SaintStageExperimentStepProvider(
      @Reference PropertyLoader properties,
      @Reference SaintStageDiagram stageDiagram,
      @Reference SaintXYStageExecutor stageExecutor) {
    this.properties = properties.getProperties(SaintProperties.class);
    this.stageDiagram = stageDiagram;
    this.stageExecutor = stageExecutor;
  }

  @Override
  public Localized<String> name() {
    return properties.stageExperimentStepName();
  }

  @Override
  public Executor<Nothing> executor() {
    return stageExecutor;
  }

  @Override
  public Stream<StepDefinition<Nothing>> createSteps(DefineStep<Nothing> defineStep) {
    // TODO set location from stage diagram, e.g. selected wells
    var step = defineStep
        .withName("Sample Position")
        .withVariables(v -> v.with(LOCATION, new XYCoordinate<>(Units.metre().getUnit(), 0, 0)));
    return Stream.of(step);
  }
}
