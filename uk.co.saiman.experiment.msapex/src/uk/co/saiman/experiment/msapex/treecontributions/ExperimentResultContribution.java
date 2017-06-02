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
package uk.co.saiman.experiment.msapex.treecontributions;

import java.util.Objects;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentResult;
import uk.co.strangeskies.eclipse.EclipseTreeContribution;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.PseudoClassTreeCellContribution;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.fx.TreeTextContribution;

/**
 * Contribution for root experiment nodes in the experiment tree
 * 
 * @author Elias N Vasylenko
 */
@Component(service = EclipseTreeContribution.class, scope = ServiceScope.PROTOTYPE)
public class ExperimentResultContribution implements EclipseTreeContribution<ExperimentResult<?>>,
		TreeTextContribution<ExperimentResult<?>>,
		PseudoClassTreeCellContribution<ExperimentResult<?>> {
	private static final String RESULT_PRESENT = "Present";

	@Inject
	@Localize
	ExperimentProperties text;

	@Override
	public <U extends ExperimentResult<?>> String getText(TreeItemData<U> data) {
		return data.data().getResultType().getName();
	}

	@Override
	public <U extends ExperimentResult<?>> String getSupplementalText(TreeItemData<U> data) {
		return "[" + data
				.data()
				.getData()
				.map(d -> Objects.toString(data.data().getResultDataPath()))
				.map(Object::toString)
				.orElse(text.missingResult().toString()) + "]";
	}

	@Override
	public <U extends ExperimentResult<?>> String getPseudoClassName(TreeItemData<U> data) {
		return PseudoClassTreeCellContribution.super.getPseudoClassName(data)
				+ (data.data().getData().isPresent() ? RESULT_PRESENT : "");
	}
}
