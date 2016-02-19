package uk.co.saiman.chemistry;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.strangeskies.modabi.SchemaManager;

/**
 * Loads the default periodic table resource and registers as a service.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class PeriodicTableService {
	@Reference
	protected SchemaManager manager;

	/**
	 * Activation registers the periodic table service.
	 * 
	 * @param context
	 *          The bundle context in which to register our service
	 */
	@Activate
	public void activate(BundleContext context) {
		new Thread(() -> {
			try {
				PeriodicTable periodicTable = manager.bind(PeriodicTable.class)
						.from(getClass().getResource("PeriodicTable.xml")).resolve();

				context.registerService(PeriodicTable.class, periodicTable, new Hashtable<>());
			} catch (Throwable e) {
				e.printStackTrace();
				throw e;
			}
		}).start();
	}
}
