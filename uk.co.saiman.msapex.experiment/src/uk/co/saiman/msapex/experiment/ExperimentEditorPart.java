/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.msapex.experiment;

import static uk.co.saiman.msapex.experiment.ExperimentEditorAddon.EDITOR_EXPERIMENT_NODE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;

import uk.co.saiman.experiment.ExperimentStep;
import uk.co.saiman.experiment.Observation;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * Experiment management view part. Manage experiments and their results in the
 * experiment tree.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentEditorPart {
  public static final String ID = "uk.co.saiman.msapex.experiment.compositepart.editor";

  @Inject
  private IEclipseContext context;
  @Inject
  private MCompositePart part;

  private ExperimentStep<?> node;

  @PostConstruct
  void initialize() {
    node = (ExperimentStep<?>) part.getTransientData().get(EDITOR_EXPERIMENT_NODE);

    context.set(ExperimentStep.class, node);
    node
        .getProcedure()
        .observations()
        .map(Observation::getResultType)
        .map(TypeToken::getErasedType)
        .forEach(context::declareModifiable);
    context.declareModifiable(node.getProcedure().getVariablesType().getErasedType());
  }
}
