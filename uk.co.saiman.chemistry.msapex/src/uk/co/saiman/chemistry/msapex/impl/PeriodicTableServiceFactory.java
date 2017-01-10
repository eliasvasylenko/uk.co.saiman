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

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.saiman.chemistry.msapex.PeriodicTableService;
import uk.co.strangeskies.utilities.ObservableProperty;

@SuppressWarnings("javadoc")
@Component(property = IContextFunction.SERVICE_CONTEXT_KEY + "=uk.co.saiman.chemistry.msapex.PeriodicTableService")
public class PeriodicTableServiceFactory implements IContextFunction {
	private final List<PeriodicTable> periodicTables = new ArrayList<>();
	private final ObservableProperty<PeriodicTable, PeriodicTable> periodicTable = ObservableProperty.over(null);

	public final class PeriodicTableServiceImpl implements PeriodicTableService {
		public PeriodicTableServiceImpl() {}

		@Override
		public ObservableProperty<PeriodicTable, PeriodicTable> periodicTable() {
			return periodicTable;
		}

		/**
		 * @return the available periodic tables
		 */
		@Override
		public Stream<PeriodicTable> periodicTables() {
			return periodicTables.stream();
		}
	}

	@Reference(cardinality = MULTIPLE)
	public synchronized void addPeriodicTable(PeriodicTable table) {
		if (periodicTables.add(table) && periodicTable.get() == null) {
			periodicTable.set(table);
		}
	}

	public synchronized void removePeriodicTable(PeriodicTable table) {
		if (periodicTables.remove(table) && periodicTable.get() == table) {
			if (periodicTables.isEmpty()) {
				periodicTable.set(null);
			} else {
				periodicTable.set(periodicTables.get(0));
			}
		}
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		PeriodicTableService service = new PeriodicTableServiceImpl();
		ContextInjectionFactory.inject(service, context);
		return service;
	}
}
