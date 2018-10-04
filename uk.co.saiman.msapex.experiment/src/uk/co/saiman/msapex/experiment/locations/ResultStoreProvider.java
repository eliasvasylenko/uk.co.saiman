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
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment.locations;

import java.util.Optional;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.experiment.ResultStore;

public interface ResultStoreProvider {
  /**
   * @return the persistent id of the provider
   */
  String getId();

  /**
   * @return the human-readable name of the provider
   */
  String getName();

  /**
   * Request a result locator from the provider. Invocation of this method may
   * present UI to gather user input to inform creation of the result locator, or
   * to allow cancellation.
   * 
   * @return an optional containing the result store, or an empty optional if the
   *         operation was cancelled
   */
  Optional<ResultStore> requestStore(IEclipseContext context);
}
