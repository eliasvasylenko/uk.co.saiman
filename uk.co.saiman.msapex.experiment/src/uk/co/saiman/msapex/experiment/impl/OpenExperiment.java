/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.impl;

import static uk.co.saiman.msapex.experiment.ExperimentPart.OPEN_EXPERIMENT_COMMAND;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.eclipse.Localize;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.spectrum.SpectrumExperimentType;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeItemImpl;
import uk.co.saiman.msapex.experiment.ExperimentPart;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * Add an experiment to the workspace.
 * 
 * @author Elias N Vasylenko
 */
public class OpenExperiment {
  @CanExecute
  boolean canExecute(MPart part, @Localize ExperimentProperties text) {
    ExperimentPartImpl experimentPart = (ExperimentPartImpl) part.getObject();

    TreeItemImpl<?> item = experimentPart.getExperimentTreeController().getSelection();

    if (item == null) {
      new ExperimentException(
          text.exception().illegalCommandForSelection(OPEN_EXPERIMENT_COMMAND, null));
    }

    TreeItemData<?> experimentItem = item.getData();

    return experimentItem.type().isAssignableTo(
        new TypeToken<ExperimentNode<? extends SpectrumExperimentType<?>, ?>>() {});
  }

  @Execute
  void execute(MPart part, ResultEditorManager editorManager) {
    ExperimentPart experimentPart = (ExperimentPart) part.getObject();

    TreeItemImpl<?> item = experimentPart.getExperimentTreeController().getSelection();

    TreeItemData<?> experimentItem = item.getData();

    @SuppressWarnings("unchecked")
    ExperimentNode<? extends SpectrumExperimentType<?>, ?> node = (ExperimentNode<? extends SpectrumExperimentType<?>, ?>) experimentItem
        .data();

    editorManager.openEditor(node.getResult(node.getType().getSpectrumResult()));
  }
}
