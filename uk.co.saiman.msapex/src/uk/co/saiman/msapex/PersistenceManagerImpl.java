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
 * This file is part of uk.co.saiman.msapex.
 *
 * uk.co.saiman.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex;

import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.cm.file.FilePersistenceManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

/**
 * Simple persistence manager implementation since the default doesn't support
 * configuration with the users home directory.
 *
 * @author Elias N Vasylenko
 */
@Component(property = Constants.SERVICE_RANKING + ":Integer=" + 100)
public class PersistenceManagerImpl extends FilePersistenceManager implements PersistenceManager {
	private static final String CONFIGURATION_DIRECTORY = System.getProperty("user.home") + "/.msapex/config";

	@SuppressWarnings("javadoc")
	public PersistenceManagerImpl() {
		super(CONFIGURATION_DIRECTORY);
	}
}
