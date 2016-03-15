/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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