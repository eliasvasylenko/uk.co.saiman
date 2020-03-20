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
package uk.co.saiman.experiment.dependency;

import java.util.function.Function;

public interface Resource<T> extends Dependency<T>, AutoCloseable {
  T value();

  @Override
  void close();

  static <T extends AutoCloseable> Resource<T> over(Class<T> provision, T value) {
    return over(provision, value, v -> v);
  }

  static <T> Resource<T> over(
      Class<T> provision,
      T value,
      Function<? super T, ? extends AutoCloseable> close) {
    return new Resource<T>() {
      boolean closed = false;

      @Override
      public Class<T> type() {
        return provision;
      }

      @Override
      public T value() {
        if (closed) {
          throw new ResourceClosedException(provision);
        }
        return value;
      }

      @Override
      public void close() {
        closed = true;
        try {
          close.apply(value).close();
        } catch (Exception e) {
          throw new ResourceClosingException(provision);
        }
      }
    };
  }
}
