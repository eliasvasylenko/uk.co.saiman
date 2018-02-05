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
 * This file is part of uk.co.saiman.msapex.experiment.spectrum.
 *
 * uk.co.saiman.msapex.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.spectrum;

import static uk.co.saiman.eclipse.treeview.DefaultContribution.setLabel;
import static uk.co.saiman.eclipse.treeview.DefaultContribution.setSupplemental;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.layout.HBox;
import uk.co.saiman.eclipse.treeview.Contributor;
import uk.co.saiman.eclipse.treeview.PseudoClassContributor;
import uk.co.saiman.eclipse.treeview.TreeChildren;
import uk.co.saiman.eclipse.treeview.TreeContribution;
import uk.co.saiman.eclipse.treeview.TreeEntry;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.processing.ProcessorState;
import uk.co.saiman.experiment.spectrum.SpectrumConfiguration;
import uk.co.saiman.reflection.token.TypeToken;

@Component(scope = ServiceScope.PROTOTYPE, property = Constants.SERVICE_RANKING + ":Integer=" + 10)
public class SpectrumExperimentNodeContribution implements TreeContribution {
  private final Contributor pseudoClass = new PseudoClassContributor(getClass().getSimpleName());

  @AboutToShow
  public void prepare(
      HBox node,
      TreeEntry<ExperimentNode<? extends SpectrumConfiguration, ?>> data,
      TreeChildren children) {
    setLabel(node, data.data().getType().getName());
    setSupplemental(node, data.data().getState().getSpectrumName());

    pseudoClass.configureCell(node);
  }
}
