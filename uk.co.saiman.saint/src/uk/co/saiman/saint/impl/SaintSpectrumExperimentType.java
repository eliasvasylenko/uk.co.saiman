package uk.co.saiman.saint.impl;

import java.util.Random;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.acquisition.AcquisitionDevice;
import uk.co.saiman.experiment.ConfigurationContext;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.sample.XYStageExperimentType;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.saint.SaintSpectrumConfiguration;
import uk.co.saiman.saint.SaintXYStageConfiguration;

@Component
public class SaintSpectrumExperimentType extends SpectrumExperimentType<SaintSpectrumConfiguration>
    implements ExperimentType<SaintSpectrumConfiguration> {
  @Reference
  XYStageExperimentType<SaintXYStageConfiguration> stageExperiment;

  @Reference
  AcquisitionDevice acquisitionDevice;

  @Override
  public String getId() {
    return getClass().getName();
  }

  @Override
  public SaintSpectrumConfiguration createState(
      ConfigurationContext<SaintSpectrumConfiguration> context) {
    SaintSpectrumConfiguration configuration = new SaintSpectrumConfiguration() {
      private String name;

      @Override
      public void setSpectrumName(String name) {
        this.name = name;
        context.setId(name);
      }

      @Override
      public String getSpectrumName() {
        return name;
      }

      @Override
      public AcquisitionDevice getAcquisitionDevice() {
        return acquisitionDevice;
      }
    };
    configuration.setSpectrumName("test-" + new Random().nextInt(Integer.MAX_VALUE));
    return configuration;
  }

  @Override
  public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return parentNode.findAncestor(stageExperiment).isPresent();
  }

  @Override
  public boolean mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?> descendantNodeType) {
    return false;
  }

  @Override
  protected AcquisitionDevice getAcquisitionDevice() {
    return acquisitionDevice;
  }
}
