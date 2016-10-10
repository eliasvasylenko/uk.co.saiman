/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex.treecontributions;

import java.util.Objects;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import javafx.scene.Node;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.saiman.simulation.msapex.ChooseSimulatedSampleImage;
import uk.co.strangeskies.eclipse.CommandTreeCellContribution;
import uk.co.strangeskies.eclipse.EclipseTreeContribution;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;

@SuppressWarnings("javadoc")
@Component(service = EclipseTreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class SampleImageContribution extends CommandTreeCellContribution<SimulatedSampleImage>
		implements PseudoClassTreeCellContribution<SimulatedSampleImage>, TreeTextContribution<SimulatedSampleImage> {
	@Inject
	@Localize
	SimulationProperties properties;

	public SampleImageContribution() {
		super(ChooseSimulatedSampleImage.COMMAND_ID);
	}

	@Override
	public <U extends SimulatedSampleImage> String getText(TreeItemData<U> data) {
		return properties.sampleImage().toString();
	}

	@Override
	public <U extends SimulatedSampleImage> String getSupplementalText(TreeItemData<U> data) {
		return Objects.toString(data.data());
	}

	@Override
	public <U extends SimulatedSampleImage> Node configureCell(TreeItemData<U> data, Node content) {
		content = PseudoClassTreeCellContribution.super.configureCell(data, content);
		return super.configureCell(data, content);
	}
}
