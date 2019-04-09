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
 * This file is part of uk.co.saiman.osgi.
 *
 * uk.co.saiman.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * A very simple wrapper around an OSGi service which exposes the service
 * ranking. The ranking is not guaranteed to remain consistent.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the service
 */
public interface ServiceRecord<S, U, T> extends Comparable<ServiceRecord<?, ?, ?>> {
  ServiceReference<S> serviceReference();

  /**
   * @return the wrapped service object
   */
  T serviceObject();

  U id();

  int rank();

  Bundle bundle();

  @Override
  default int compareTo(ServiceRecord<?, ?, ?> that) {
    return -Integer.compare(this.rank(), that.rank());
  }
}
