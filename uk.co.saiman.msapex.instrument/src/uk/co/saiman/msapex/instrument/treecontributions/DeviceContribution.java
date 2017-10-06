package uk.co.saiman.msapex.instrument.treecontributions;

import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.Node;
import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentExecutionContext;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Result;
import uk.co.saiman.experiment.ResultType;
import uk.co.saiman.experiment.Workspace;
import uk.co.saiman.fx.TreeCellContribution;
import uk.co.saiman.fx.TreeChildContribution;
import uk.co.saiman.fx.TreeContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeTextContribution;
import uk.co.saiman.instrument.Device;
import uk.co.saiman.observable.Observable;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.reflection.token.TypedReference;

@Component(service = TreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class DeviceContribution implements TreeCellContribution<Device>,
    TreeTextContribution<Device>, TreeChildContribution<Device> {
  @Override
  public <U extends Device> Node configureCell(TreeItemData<U> item, Node content) {
    return configurePseudoClass(
        configurePseudoClass(content, item.data().connectionState().get().toString()));
  }

  @Override
  public <U extends Device> String getText(TreeItemData<U> item) {
    return item.data().getName();
  }

  @Override
  public <U extends Device> String getSupplementalText(TreeItemData<U> item) {
    return item.data().connectionState().get().toString();
  }

  @Override
  public <U extends Device> Stream<TypedReference<?>> getChildren(TreeItemData<U> data) {
    return Stream.of(TypedReference.typedObject(Experiment.class, new Experiment() {
      @Override
      public void remove() {
        // TODO Auto-generated method stub

      }

      @Override
      public void process() {
        // TODO Auto-generated method stub

      }

      @Override
      public ObservableValue<ExperimentLifecycleState> lifecycleState() {
        return Observable.value(ExperimentLifecycleState.FAILURE);
      }

      @Override
      public ExperimentRoot getType() {
        return new ExperimentRoot() {
          @Override
          public boolean mayComeBefore(
              ExperimentNode<?, ?> penultimateDescendantNode,
              ExperimentType<?> descendantNodeType) {
            return false;
          }

          @Override
          public boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
            return false;
          }

          @Override
          public String getName() {
            return "roooot";
          }

          @Override
          public void execute(ExperimentExecutionContext<ExperimentConfiguration> context) {}

          @Override
          public ExperimentConfiguration createState(
              ExperimentConfigurationContext<ExperimentConfiguration> context) {
            // TODO Auto-generated method stub
            return null;
          }
        };
      }

      @Override
      public ExperimentConfiguration getState() {
        return new ExperimentConfiguration() {
          
          @Override
          public void setNotes(String notes) {
            // TODO Auto-generated method stub
            
          }
          
          @Override
          public void setName(String name) {
            // TODO Auto-generated method stub
            
          }
          
          @Override
          public Optional<String> getNotes() {
            // TODO Auto-generated method stub
            return null;
          }
          
          @Override
          public String getName() {
            return "stupidhead";
          }
          
          @Override
          public void clearNotes() {
            // TODO Auto-generated method stub
            
          }
        };
      }

      @Override
      public Stream<Result<?>> getResults() {
        return Stream.empty();
      }

      @Override
      public <U> Result<U> getResult(ResultType<U> resultType) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Optional<ExperimentNode<?, ?>> getParent() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getID() {
        return "stupid";
      }

      @Override
      public Workspace getExperimentWorkspace() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Stream<ExperimentNode<?, ?>> getChildren() {
        return Stream.empty();
      }

      @Override
      public Stream<ExperimentType<?>> getAvailableChildExperimentTypes() {
        return Stream.empty();
      }

      @Override
      public void clearResults() {
        // TODO Auto-generated method stub

      }

      @Override
      public <U, E extends ExperimentType<U>> ExperimentNode<E, U> addChild(E childType) {
        // TODO Auto-generated method stub
        return null;
      }
    }));
  }
}
