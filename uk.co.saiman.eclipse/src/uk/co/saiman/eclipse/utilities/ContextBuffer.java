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
 * This file is part of uk.co.saiman.eclipse.
 *
 * uk.co.saiman.eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ContextBuffer {
  private final Map<String, Object> values = new HashMap<>();

  public static ContextBuffer empty() {
    return new ContextBuffer();
  }

  public ContextBuffer set(String name, Object value) {
    values.put(name, value);
    return this;
  }

  public <T> ContextBuffer set(Class<T> clazz, T value) {
    return set(clazz.getName(), value);
  }

  public Stream<String> keys() {
    return values.keySet().stream();
  }

  public Object get(String name) {
    return values.get(name);
  }

  public <T> T get(Class<T> clazz) {
    return clazz.cast(values.get(clazz.getName()));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + values.toString();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ContextBuffer)) {
      return false;
    }
    var that = (ContextBuffer) obj;
    return Objects.equals(this.values, that.values);
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }
}
