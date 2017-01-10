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
 * This file is part of uk.co.saiman.chemistry.msapex.
 *
 * uk.co.saiman.chemistry.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry.msapex.impl;

import static java.util.Optional.of;
import static uk.co.saiman.chemistry.Chemical.unknownChemical;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.chemistry.Chemical;
import uk.co.saiman.chemistry.msapex.ChemicalSelectionRequester;
import uk.co.saiman.chemistry.msapex.PeriodicTableService;

@SuppressWarnings("javadoc")
@Component(
		property = IContextFunction.SERVICE_CONTEXT_KEY + "=uk.co.saiman.chemistry.msapex.ChemicalSelectionRequester")
public class ChemicalSelectionRequesterFactory implements IContextFunction {
	static final class ChemicalSelectionRequesterImpl implements ChemicalSelectionRequester {
		@Inject
		PeriodicTableService periodicTables;

		@Override
		public Optional<Chemical> requestChemical() {
			return of(unknownChemical());
		}
	}

	@Override
	public ChemicalSelectionRequester compute(IEclipseContext context, String contextKey) {
		return ContextInjectionFactory.make(ChemicalSelectionRequesterImpl.class, context);
	}
}
