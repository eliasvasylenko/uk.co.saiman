package uk.co.saiman.msapex.experiment.spectrum;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.Service;

import uk.co.saiman.data.spectrum.SpectrumProcessor;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;

public class AddProcessorHandler {
  @Execute
  void execute(
      @Optional ExperimentNode<? extends SpectrumConfiguration, ?> node,
      @Optional @Service List<SpectrumProcessor> processors) {
    System.out.println("available processors: " + processors);
    node.getState().getProcessing().add(processors.get(0));
  }
}
