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
 * This file is part of uk.co.saiman.reflection.
 *
 * uk.co.saiman.reflection is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A collection of utility methods relating to parameterized types.
 * 
 * @author Elias N Vasylenko
 */
public class ParameterizedTypes {
  static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private static final long serialVersionUID = 1L;

    private final Type ownerType;
    private final List<Type> typeArguments;
    private final Class<?> rawType;

    private Integer hashCode;

    ParameterizedTypeImpl(Type ownerType, Class<?> rawType, List<Type> typeArguments) {
      this.ownerType = ownerType;
      this.rawType = rawType;
      this.typeArguments = typeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
      return typeArguments.toArray(new Type[typeArguments.size()]);
    }

    @Override
    public Type getRawType() {
      return rawType;
    }

    @Override
    public Type getOwnerType() {
      return ownerType;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      if (ownerType != null) {
        builder.append(ownerType).append(".");
      }
      builder.append(rawType.getName());
      if (!typeArguments.isEmpty()) {
        builder
            .append("<")
            .append(typeArguments.stream().map(Objects::toString).collect(joining(", ")))
            .append(">");
      }
      return builder.toString();
    }

    @Override
    public int hashCode() {
      if (hashCode == null) {
        /*
         * Calculate the hash code properly, now we're guarded against recursion:
         */
        this.hashCode = Objects.hashCode(ownerType)
            ^ Objects.hashCode(rawType)
            ^ Objects.hashCode(typeArguments);
      }

      return hashCode;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this)
        return true;
      if (!(other instanceof ParameterizedType))
        return false;

      ParameterizedType that = (ParameterizedType) other;

      return Objects.equals(this.getRawType(), that.getRawType())
          && Objects.equals(this.getOwnerType(), that.getOwnerType())
          && Objects.equals(this.getActualTypeArguments(), that.getActualTypeArguments());
    }
  }

  private ParameterizedTypes() {}

  /**
   * This method retrieves a list of all type variables present on the given raw
   * type, as well as all type variables on any enclosing types recursively, in
   * the order encountered.
   *
   * @param rawType The class whose generic type parameters we wish to determine.
   * @return A list of all relevant type variables.
   */
  public static Stream<TypeVariable<?>> getAllTypeParameters(Class<?> rawType) {
    Stream<TypeVariable<?>> typeParameters = Stream.empty();

    do {
      typeParameters = Stream.concat(Arrays.stream(rawType.getTypeParameters()), typeParameters);
    } while (!Types.isStatic(rawType) && (rawType = rawType.getEnclosingClass()) != null);

    return typeParameters;
  }

  /**
   * For a given parameterized type, we retrieve a mapping of all type variables
   * on its raw type, as given by {@link #getAllTypeParameters(Class)}, to their
   * arguments within the context of this type.
   *
   * @param type The type whose generic type arguments we wish to determine.
   * @return A mapping of all type variables to their arguments in the context of
   *         the given type.
   */
  public static Stream<Map.Entry<TypeVariable<?>, Type>> getAllTypeArguments(
      ParameterizedType type) {
    Stream<Entry<TypeVariable<?>, Type>> typeArguments = Stream.empty();

    Class<?> rawType = (Class<?>) type.getRawType();
    do {
      TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
      Type[] actualTypeArguments = type.getActualTypeArguments();

      typeArguments = Stream
          .concat(
              IntStream
                  .range(0, typeParameters.length)
                  .mapToObj(
                      i -> new AbstractMap.SimpleEntry<>(
                          typeParameters[i],
                          actualTypeArguments[i])),
              typeArguments);

      type = type.getOwnerType() instanceof ParameterizedType
          ? (ParameterizedType) type.getOwnerType()
          : null;
      rawType = Types.isStatic(rawType) ? null : rawType.getEnclosingClass();

      if (rawType != null && type == null) {
        do {
          typeArguments = Stream
              .concat(
                  typeArguments,
                  Arrays
                      .stream(rawType.getTypeParameters())
                      .map(p -> new AbstractMap.SimpleEntry<>(p, p)));
        } while ((rawType = Types.isStatic(rawType) ? null : rawType.getEnclosingClass()) != null);
      }
    } while (type != null && rawType != null);

    return typeArguments;
  }

  /**
   * Derive an instance of {@link ParameterizedType} from a raw {@link Class}
   * using the given generic type arguments and owning type. Type parameters with
   * no provided argument will be parameterized with the type variables
   * themselves.
   * 
   * @param ownerType     the owner type for the resulting parameterized type
   * @param rawType       A raw {@link Class} from which we wish to determine a
   *                      {@link ParameterizedType}.
   * @param typeArguments A mapping of generic type variables to arguments.
   * @return A {@link ParameterizedType} instance over the given class,
   *         parameterized with the given type arguments.
   */
  public static ParameterizedType parameterize(
      Type ownerType,
      Class<?> rawType,
      List<Type> typeArguments) {
    return new ParameterizedTypeImpl(ownerType, rawType, new ArrayList<>(typeArguments));
  }

  /**
   * As @see {@link #parameterize(Class, Type[])}, but without checking type
   * arguments for consistency.
   */
  @SuppressWarnings("javadoc")
  public static ParameterizedType parameterize(Class<?> rawType, Type... typeArguments) {
    return parameterize(rawType, Arrays.asList(typeArguments));
  }

  /**
   * As @see {@link #parameterize(Class, List)}, but without checking type
   * arguments for consistency.
   */
  @SuppressWarnings("javadoc")
  public static ParameterizedType parameterize(Class<?> rawType, List<Type> typeArguments) {
    List<TypeVariable<?>> parameters = getAllTypeParameters(rawType).collect(Collectors.toList());

    if (parameters.size() != typeArguments.size()) {
      throw new IllegalArgumentException(
          "Arguments don't match parameters "
              + typeArguments
              + " - "
              + asList(rawType.getTypeParameters()));
    }

    return parameterizeImpl(rawType, typeArguments);
  }

  private static ParameterizedType parameterizeImpl(Class<?> rawType, List<Type> typeArguments) {
    int totalArgumentCount = typeArguments.size();
    int parametersOnTypeCount = rawType.getTypeParameters().length;
    int parametersOnOwnerCount = totalArgumentCount - parametersOnTypeCount;

    Type owner = rawType.getEnclosingClass();

    if (totalArgumentCount > parametersOnTypeCount) {
      owner = parameterizeImpl((Class<?>) owner, typeArguments.subList(0, parametersOnOwnerCount));

      typeArguments = typeArguments.subList(parametersOnOwnerCount, totalArgumentCount);
    }

    return parameterize(owner, rawType, typeArguments);
  }

  /**
   * As @see {@link #parameterize(Class, Function)}, but without checking type
   * arguments for consistency.
   */
  @SuppressWarnings("javadoc")
  public static <T> ParameterizedType parameterize(
      Class<T> rawType,
      Function<? super TypeVariable<?>, ? extends Type> typeArguments) {
    return (ParameterizedType) parameterizeImpl(rawType, typeArguments);
  }

  private static <T> Type parameterizeImpl(
      Class<T> rawType,
      Function<? super TypeVariable<?>, ? extends Type> typeArguments) {
    Class<?> enclosing = rawType.getEnclosingClass();
    Type ownerType;
    if (enclosing == null || Types.isStatic(rawType)) {
      ownerType = enclosing;
    } else {
      ownerType = parameterizeImpl(enclosing, typeArguments);
    }

    if ((ownerType == null || ownerType instanceof Class)
        && rawType.getTypeParameters().length == 0)
      return rawType;

    return new ParameterizedTypeImpl(ownerType, rawType, argumentsForClass(rawType, typeArguments));
  }

  /**
   * Derive an instance of {@link ParameterizedType} from a raw {@link Class},
   * substituting the type parameters of that class as their own argument
   * instantiations.
   * 
   * @param rawType A raw {@link Class} from which we wish to determine a
   *                {@link ParameterizedType}.
   * @return A {@link ParameterizedType} instance over the given class.
   */
  public static ParameterizedType parameterize(Class<?> rawType) {
    return parameterize(rawType, i -> null);
  }

  private static List<Type> argumentsForClass(
      Class<?> rawType,
      Function<? super TypeVariable<?>, ? extends Type> typeArguments) {
    List<Type> arguments = new ArrayList<>();
    for (int i = 0; i < rawType.getTypeParameters().length; i++) {
      Type argument = typeArguments.apply(rawType.getTypeParameters()[i]);
      arguments.add((argument != null) ? argument : rawType.getTypeParameters()[i]);
    }
    return arguments;
  }
}
