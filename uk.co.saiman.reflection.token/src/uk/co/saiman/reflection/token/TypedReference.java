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
 * This file is part of uk.co.saiman.reflection.token.
 *
 * uk.co.saiman.reflection.token is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection.token is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection.token;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author Elias N Vasylenko
 * 
 *         Facilitates the ability to track the exact type of an object in cases
 *         where it would normally be erased and so unavailable through
 *         reflection.
 *
 * @param <T>
 *          The type of the object instance to track.
 */
public class TypedReference<T> {
  private final TypeToken<T> type;
  private final T object;

  /**
   * @param type
   *          The exact type of an object to keep track of.
   * @param object
   *          An object reference of the given type.
   */
  private TypedReference(TypeToken<T> type, T object) {
    Objects.requireNonNull(type);

    this.type = type;
    this.object = object;
  }

  /**
   * Convenience method to return a {@link TypedReference} wrapper around an object
   * instance of this type.
   * 
   * @param object
   *          The object to wrap with a typed container
   * @return A typed container for the given object
   */
  public static <T> TypedReference<T> typedObject(TypeToken<T> type, T object) {
    return new TypedReference<>(type, object);
  }

  /**
   * Convenience method to return a {@link TypedReference} wrapper around an object
   * instance of this type.
   * 
   * @param object
   *          The object to wrap with a typed container
   * @return A typed container for the given object
   */
  public static <T> TypedReference<T> typedObject(Class<T> type, T object) {
    return typedObject(forType(type), object);
  }

  /**
   * @return The type of the reference.
   */
  public TypeToken<T> getTypeToken() {
    return type;
  }

  /**
   * @return The type of the reference.
   */
  public Type getType() {
    return getTypeToken().getType();
  }

  /**
   * @return An object reference guaranteed to be of the given type.
   */
  public T getObject() {
    return object;
  }

  @Override
  public String toString() {
    return object + ": " + type;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof TypedReference<?>))
      return false;

    TypedReference<?> that = (TypedReference<?>) obj;

    return Objects.equals(this.object, that.object) && Objects.equals(this.type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(object, type);
  }
}
