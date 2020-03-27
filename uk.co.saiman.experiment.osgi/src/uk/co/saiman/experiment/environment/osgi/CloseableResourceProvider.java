/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.osgi.
 *
 * uk.co.saiman.experiment.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.environment.osgi;

import static java.util.function.Function.identity;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.experiment.environment.GlobalEnvironment;

public interface CloseableResourceProvider<T extends AutoCloseable>
    extends ExclusiveResourceProvider<T> {
  T deriveValue(GlobalEnvironment globalEnvironment, long timeout, TimeUnit unit) throws Exception;

  @Override
  default ExclusiveResource<T> deriveResource(
      GlobalEnvironment globalEnvironment,
      long timeout,
      TimeUnit unit) throws Exception {
    var value = deriveValue(globalEnvironment, timeout, unit);
    return new ExclusiveResource<>(getProvision(), value, identity());
  }
}