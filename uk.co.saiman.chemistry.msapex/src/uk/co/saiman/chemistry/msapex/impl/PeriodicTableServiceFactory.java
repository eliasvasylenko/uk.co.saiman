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
