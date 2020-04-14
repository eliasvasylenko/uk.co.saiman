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

import java.util.stream.Stream;

/**
 * The shared environment consists of only shared resources, i.e. those
 * resources which may be acquired in parallel with no access restrictions.
 * <p>
 * Shared resources do not support exclusive access, which means all consumers
 * are always guaranteed immediate access and don't have to worry about cleaning
 * up after themselves by closing a resource.
 * 
 * @author Elias N Vasylenko
 */
public interface SharedEnvironment {
  public static SharedEnvironment EMPTY = new SharedEnvironment() {
    @Override
    public Stream<Class<?>> sharedResources() {
      return Stream.empty();
    }

    @Override
    public <T> T provideSharedResource(Class<T> provision) {
      throw new ResourceMissingException(provision);
    }
  };

  Stream<Class<?>> sharedResources();

  default boolean providesSharedResource(Class<?> provision) {
    return sharedResources().anyMatch(provision::equals);
  }

  <T> T provideSharedResource(Class<T> provision);
}
