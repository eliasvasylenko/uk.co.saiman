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
@Component(property = IContextFunction.SERVICE_CONTEXT_KEY
		+ "=uk.co.saiman.chemistry.msapex.ChemicalSelectionRequester")
public class ChemicalSelectionRequesterFactory implements IContextFunction {
	private final class ChemicalSelectionRequesterImpl implements ChemicalSelectionRequester {
		@Inject
		PeriodicTableService periodicTables;

		@Override
		public Optional<Chemical> requestChemical() {
			return of(unknownChemical());
		}
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		return ContextInjectionFactory.make(ChemicalSelectionRequesterImpl.class, context);
	}
}
