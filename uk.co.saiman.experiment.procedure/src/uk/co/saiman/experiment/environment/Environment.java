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

import uk.co.saiman.experiment.dependency.Resource;

/**
 * An environment consists of both shared and exclusive resources. The former
 * may be acquired in parallel with no access restrictions, while the latter may
 * only be acquired serially with exclusive access.
 * <p>
 * Note that the environment itself does not ensure that access to any resources
 * is exclusive! It is the responsibility of the consumer to mediate access to
 * resources according to whether they are exclusive or shared.
 * 
 * @author Elias N Vasylenko
 */
public interface Environment extends SharedEnvironment {
  public static Environment EMPTY = new Environment() {
    @Override
    public Stream<Class<?>> sharedResources() {
      return Stream.empty();
    }

    @Override
    public Stream<Class<?>> exclusiveResources() {
      return Stream.empty();
    }

    @Override
    public Stream<Class<?>> resources() {
      return Stream.empty();
    }

    @Override
    public <T> Resource<T> provideResource(Class<T> provision) {
      throw new ResourceMissingException(provision);
    }

    @Override
    public <T> T provideSharedResource(Class<T> provision) {
      throw new ResourceMissingException(provision);
    }
  };

  Stream<Class<?>> exclusiveResources();

  Stream<Class<?>> resources();

  default boolean providesResource(Class<?> provision) {
    return resources().anyMatch(provision::equals);
  }

  <T> Resource<T> provideResource(Class<T> provision);
}
