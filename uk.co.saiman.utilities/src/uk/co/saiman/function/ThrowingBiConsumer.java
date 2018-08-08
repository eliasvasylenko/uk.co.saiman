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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.function;

import java.util.function.BiConsumer;

/**
 * As {@link BiConsumer} but parameterized over an exception type which is
 * allowed to be thrown by {@link #accept(Object,Object)}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the item to accept
 * @param <E>
 *          the type of exception which may be thrown
 */
public interface ThrowingBiConsumer<T, U, E extends Throwable> {
  /**
   * @param value
   *          an instance of the expected type
   * @throws E
   *           an exception thrown by the implementor
   */
  void accept(T t, U u) throws E;
}
