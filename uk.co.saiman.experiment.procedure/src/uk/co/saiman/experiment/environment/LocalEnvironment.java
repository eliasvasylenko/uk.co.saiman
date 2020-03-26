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
 * This file is part of uk.co.saiman.experiment.declaration.
 *
 * uk.co.saiman.experiment.declaration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.declaration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.environment;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import uk.co.saiman.experiment.dependency.Resource;

public interface LocalEnvironment extends AutoCloseable {
  LocalEnvironment EMPTY = new LocalEnvironment() {
    @Override
    public void acquireResources(
        java.util.Collection<? extends java.lang.Class<?>> resources,
        long timeout,
        TimeUnit unit) {
      resources.stream().findAny().ifPresent(type -> {
        throw new ResourceUnavailableException(type);
      });
    }

    @Override
    public void close() {}

    @Override
    public Stream<Class<?>> providedResources() {
      return Stream.empty();
    }

    @Override
    public <T> Resource<T> provideResource(Class<T> provision) {
      throw new ResourceMissingException(provision);
    }

    @Override
    public GlobalEnvironment getGlobalEnvironment() {
      return GlobalEnvironment.EMPTY;
    }
  };

  GlobalEnvironment getGlobalEnvironment();

  /**
   * If the resources cannot be acquired then an exception is thrown. Previously
   * acquired resources continue to be held. Clients should take care not to try
   * to keep waiting for the new resources to be available and retrying
   * acquisition if they already hold some, as this may cause deadlock.
   * 
   * @param resources
   * @param timeout
   * @param unit
   */
  public void acquireResources(
      Collection<? extends Class<?>> resources,
      long timeout,
      TimeUnit unit);

  Stream<Class<?>> providedResources();

  default boolean providesResource(Class<?> provision) {
    return providedResources().anyMatch(provision::equals);
  }

  <T> Resource<T> provideResource(Class<T> provision);
}
