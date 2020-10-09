/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex;

import static javafx.css.PseudoClass.getPseudoClass;
import static uk.co.saiman.eclipse.ui.TransferMode.COPY;
import static uk.co.saiman.eclipse.ui.TransferMode.DISCARD;
import static uk.co.saiman.eclipse.ui.TransferMode.MOVE;

import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;

import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.ui.BeginTransfer;
import uk.co.saiman.eclipse.ui.Children;
import uk.co.saiman.eclipse.ui.CompleteTransfer;
import uk.co.saiman.eclipse.ui.Remove;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferSink;
import uk.co.saiman.eclipse.ui.TransferSource;
import uk.co.saiman.eclipse.ui.fx.impl.TransferDestination;
import uk.co.saiman.eclipse.utilities.ContextBuffer;
import uk.co.saiman.eclipse.utilities.EclipseContextUtilities;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.declaration.ExperimentPath;
import uk.co.saiman.experiment.design.ExperimentStepDesign;
import uk.co.saiman.experiment.design.json.JsonStepDesignFormat;
import uk.co.saiman.experiment.event.AddStepEvent;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.event.MoveStepEvent;
import uk.co.saiman.experiment.event.RemoveStepEvent;
import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.experiment.msapex.i18n.ExperimentProperties;
import uk.co.saiman.experiment.procedure.Instruction;
import uk.co.saiman.experiment.variables.Variables;
import uk.co.saiman.msapex.editor.EditorService;

/**
 * Contribution for all experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentStepCell {
  public static final String ID = "uk.co.saiman.experiment.step.cell";
  public static final String SUPPLEMENTAL_TEXT = ID + ".supplemental";
  public static final PseudoClass SUPPLEMENTAL_PSEUDO_CLASS = getPseudoClass(
      SUPPLEMENTAL_TEXT.replace('.', '-'));

  public static final String PARENT_STEP = "uk.co.saiman.experiment.step.cell.parent";

  @Inject
  @Service
  private ExperimentProperties text;
  @Inject
  private EditorService editorService;
  @Inject
  private IEclipseContext context;
  @Inject
  private Step step;

  private Label supplementalText = new Label();
  private Label lifecycleIndicator = new Label();

  @Inject
  private MCell cell;

  private TransferFormat<ExperimentStepDesign> stepTransferFormat;

  @CanExecute
  public boolean canExecute() {
    return editorService.getApplicableEditors(Step.class, step).findFirst().isPresent();
  }

  @Execute
  public void execute() {
    try {
      editorService
          .getApplicableEditors(Step.class, step)
          .findFirst()
          .ifPresent(editorService::open);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @PostConstruct
  public void prepare(HBox node, ExecutorService executorService) {
    /*
     * configure label
     */
    node.getChildren().add(supplementalText);
    supplementalText.pseudoClassStateChanged(SUPPLEMENTAL_PSEUDO_CLASS, true);
    context.set(SUPPLEMENTAL_TEXT, supplementalText);

    /*
     * add spacer
     */
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.SOMETIMES);
    node.getChildren().add(spacer);

    /*
     * label to show lifecycle state icon
     */
    node.getChildren().add(lifecycleIndicator);

    /*
     * Inject configuration
     */
    context.set(ExperimentStepDesign.class, step.getDesign());
    context.set(Instruction.class, step.getInstruction());
    context.set(Variables.class, step.getVariables());
    context.set(ExperimentPath.class, step.getPath());
    context.set(Executor.class, step.getExecutor());

    EclipseContextUtilities.injectSubtypes(context, Executor.class);

    EclipseContextUtilities
        .injectDerived(
            context,
            Variables.class,
            (v, c) -> step
                .getVariableDeclarations()
                .forEach(dec -> c.set(dec.variable().id(), new IContextFunction() {
                  String id = dec.variable().id();

                  @Override
                  public Object compute(IEclipseContext context, String contextKey) {
                    if (contextKey.equals(id)) {
                      return context.get(Step.class).getVariable(dec.variable());
                    } else {
                      return null;
                    }
                  }
                })));

    /*
     * Inject events
     */

    /*
     * Children
     */
    updatePath();

    /*
     * Transfer
     */
    stepTransferFormat = new TransferFormat<>(
        new JsonStepDesignFormat(executorService),
        EnumSet.of(COPY, MOVE, DISCARD));
  }

  @BeginTransfer
  public TransferSource beginTransfer() {
    System.out.println(" begin transfer at " + step);
    return new TransferSource().with(stepTransferFormat, step.getDesign());
  }

  @CompleteTransfer
  public TransferSink completeTransfer(
      TransferDestination destination,
      @Optional @Named(CompleteTransfer.SIBLING) MCell sibling) {
    return new TransferSink().with(stepTransferFormat, definition -> {
      int index;
      if (sibling != null && sibling.getContext().get(PARENT_STEP) == step) {
        index = sibling.getContext().get(Step.class).getIndex();
        if (destination == TransferDestination.AFTER_CHILD) {
          index++;
        }
      } else {
        index = (int) step.getDependentSteps().count();
      }
      step.attach(index, definition);
    });
  }

  @Remove
  public void remove() {
    step.detach();
  }

  @Inject
  @Optional
  public void update(MoveStepEvent event) {
    if (Objects.equals(event.step(), step)) {
      updatePath();
    }
  }

  @Inject
  @Optional
  public void update(ChangeVariableEvent event) {
    if (Objects.equals(event.step(), step)) {
      // TODO update context?
    }
  }

  public void updatePath() {
    cell.setLabel(step.getId().name());
    context.set(ExperimentPath.class, step.getPath());
  }

  @Children(snippetId = ExperimentStepCell.ID)
  private Stream<? extends ContextBuffer> updateChildren(
      @Optional AddStepEvent addition,
      @Optional RemoveStepEvent removal) {
    var hasAddition = addition != null
        && addition.dependencyStep().filter(step::equals).isPresent();
    var hasRemoval = removal != null
        && removal.previousDependencyStep().filter(step::equals).isPresent();
    if (!hasAddition && !hasRemoval) {
      return null;
    }
    return updateChildren();
  }

  @Children(snippetId = ExperimentStepCell.ID)
  private Stream<? extends ContextBuffer> updateChildren() {
    return step
        .getDependentSteps()
        .map(step -> ContextBuffer.empty().set(Step.class, step).set(PARENT_STEP, this.step));
  }
}
