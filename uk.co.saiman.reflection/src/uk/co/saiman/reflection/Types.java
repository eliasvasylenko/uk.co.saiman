/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static uk.co.saiman.collection.StreamUtilities.entriesToMap;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;
import static uk.co.saiman.reflection.ParameterizedTypes.getAllTypeArguments;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A collection of general utility methods relating to the Java type system.
 * Some utilities related to more specific classes of type may be found in
 * {@link WildcardTypes}, {@link ParameterizedTypes}, and {@link ArrayTypes}..
 * 
 * @author Elias N Vasylenko
 */
public final class Types {
  private static final Map<Class<?>, Class<?>> WRAPPED_PRIMITIVES = Collections
      .unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
        private static final long serialVersionUID = 1L;

        {
          put(void.class, Void.class);
          put(boolean.class, Boolean.class);
          put(byte.class, Byte.class);
          put(char.class, Character.class);
          put(short.class, Short.class);
          put(int.class, Integer.class);
          put(long.class, Long.class);
          put(float.class, Float.class);
          put(double.class, Double.class);
        }
      });

  private static final Map<Class<?>, Class<?>> UNWRAPPED_PRIMITIVES = Collections
      .unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
        private static final long serialVersionUID = 1L;

        {
          for (Class<?> primitive : WRAPPED_PRIMITIVES.keySet())
            put(WRAPPED_PRIMITIVES.get(primitive), primitive);
        }
      });

  private Types() {}

  /**
   * Determine whether a {@link Class} represents a generic class or an array with
   * a generic class as a component type.
   * 
   * @param type
   *          the type we wish to classify
   * @return true if the given class is generic or if a non-statically enclosing
   *         class is generic, false otherwise
   */
  public static boolean isGeneric(Class<?> type) {
    while (type.isArray()) {
      type = type.getComponentType();
    }

    do {
      if (type.getTypeParameters().length > 0)
        return true;
    } while (!Types.isStatic(type) && (type = type.getEnclosingClass()) != null);

    return false;
  }

  /**
   * Determine whether a type is raw, i.e. a generic type without a
   * parameterization.
   * 
   * @param type
   *          the type we wish to classify
   * @return true if the given class is raw or if a non-statically enclosing class
   *         is raw, false otherwise
   */
  public static boolean isRaw(Type type) {
    return isErasure(type) && isGeneric((Class<?>) type);
  }

  /**
   * Determine whether a type is the erasure of itself, i.e. if it is either raw
   * or a non-generic type.
   * 
   * @param type
   *          the type we wish to classify
   * @return true if the given class is raw or if a non-statically enclosing class
   *         is raw, false otherwise
   */
  public static boolean isErasure(Type type) {
    return type instanceof Class<?>;
  }

  /**
   * Get the erasure of the given type.
   * 
   * @param type
   *          The type of which we wish to determine the raw type.
   * @return The raw type of the type represented by this TypeToken.
   */
  public static Class<?> getErasedType(Type type) {
    if (type == null) {
      return null;
    } else if (type instanceof TypeVariable<?>) {
      Type[] bounds = ((TypeVariable<?>) type).getBounds();
      if (bounds.length == 0)
        return Object.class;
      else
        return getErasedType(bounds[0]);
    } else if (type instanceof WildcardType) {
      Type[] bounds = ((WildcardType) type).getUpperBounds();
      if (bounds.length == 0)
        return Object.class;
      else
        return getErasedType(bounds[0]);
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof Class) {
      return (Class<?>) type;
    } else if (type instanceof GenericArrayType) {
      return Array
          .newInstance(getErasedType(((GenericArrayType) type).getGenericComponentType()), 0)
          .getClass();
    } else {
      return Object.class;
    }
  }

  /**
   * Find the upper bounding classes and parameterized types of a given type.
   * 
   * @param type
   *          The type whose bounds we wish to discover.
   * @return The upper bounds of the given type.
   */
  public static Stream<Type> getUpperBounds(Type type) {
    Type[] types;

    if (type instanceof WildcardType)
      types = ((WildcardType) type).getUpperBounds();

    else if (type instanceof TypeVariable)
      types = ((TypeVariable<?>) type).getBounds();

    else
      return of(type);

    return stream(types).flatMap(Types::getUpperBounds);
  }

  /**
   * Get all primitive type classes
   * 
   * @return A set containing all primitive types.
   */
  public static Stream<Class<?>> getPrimitives() {
    return WRAPPED_PRIMITIVES.keySet().stream();
  }

  /**
   * Is the given type a primitive type as per the Java type system.
   * 
   * @param type
   *          The type we wish to classify.
   * @return True if the type is primitive, false otherwise.
   */
  public static boolean isPrimitive(Type type) {
    return WRAPPED_PRIMITIVES.keySet().contains(type);
  }

  /**
   * Is the type a wrapper for a primitive type as per the Java type system.
   * 
   * @param type
   *          The type we wish to classify.
   * @return True if the type is a primitive wrapper, false otherwise.
   */
  public static boolean isPrimitiveWrapper(Type type) {
    return UNWRAPPED_PRIMITIVES.keySet().contains(type);
  }

  /**
   * If this TypeToken is a primitive type, determine the wrapped primitive type.
   * 
   * @param <T>
   *          The type we wish to wrap.
   * @param type
   *          The type we wish to wrap.
   * @return The wrapper type of the primitive type this TypeToken represents,
   *         otherwise this TypeToken itself.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Type> T wrapPrimitive(T type) {
    if (isPrimitive(type))
      return (T) WRAPPED_PRIMITIVES.get(type);
    else
      return type;
  }

  /**
   * If this TypeToken is a wrapper of a primitive type, determine the unwrapped
   * primitive type.
   * 
   * @param <T>
   *          The type we wish to unwrap.
   * @param type
   *          The type we wish to unwrap.
   * @return The primitive type wrapped by this TypeToken, otherwise this
   *         TypeToken itself.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Type> T unwrapPrimitive(T type) {
    if (isPrimitiveWrapper(type))
      return (T) UNWRAPPED_PRIMITIVES.get(type);
    else
      return type;
  }

  /**
   * Determine whether a given class is static.
   * 
   * @param rawType
   *          The type we wish to classify.
   * @return True if the type is static, false otherwise.
   */
  public static boolean isStatic(Class<?> rawType) {
    return Modifier.isStatic(rawType.getModifiers());
  }

  /**
   * Determine if the given type, {@code from}, contains the given type,
   * {@code to}. In other words, if either of the given types are wildcards,
   * determine if every possible instantiation of {@code to} is also a valid
   * instantiation of {@code from}. Or if neither type is a wildcard, determine
   * whether both types are assignable to each other as per
   * {@link Types#isSubtype(Type, Type)}.
   * 
   * @param from
   *          the type within which we are determining containment
   * @param to
   *          the type of which we are determining containment
   * @return true if {@code from} <em>contains</em> {@code to}, false otherwise
   */
  public static boolean isContainedBy(Type from, Type to) {
    boolean contained;

    if (to.equals(from)) {
      contained = true;
    } else if (to instanceof WildcardType) {
      WildcardType toWildcard = (WildcardType) to;

      contained = isSubtype(from, toWildcard.getUpperBounds());

      contained = contained
          && (toWildcard.getLowerBounds().length == 0
              || isSubtype(toWildcard.getLowerBounds(), from));
    } else {
      contained = isSubtype(from, to) && isSubtype(to, from);
    }

    return contained;
  }

  private static boolean isSubtype(Type subtype, Type[] supertypes) {
    return Arrays.stream(supertypes).allMatch(t -> isSubtype(subtype, t));
  }

  private static boolean isSubtype(Type[] subtypes, Type supertype) {
    if (subtypes.length == 0) {
      return isSubtype(Object.class, supertype);
    } else {
      return Arrays.stream(subtypes).anyMatch(f -> isSubtype(f, supertype));
    }
  }

  /**
   * Determine if a given type, {@code supertype}, is a subtype of another given
   * type, {@code subtype}. Or in other words, if {@code supertype} is a supertype
   * of {@code subtype}. Types are considered assignable if they involve unchecked
   * generic casts.
   * 
   * @param subtype
   *          the type from which we wish to determine assignability
   * @param supertype
   *          the type to which we wish to determine assignability
   * @return true if the types are assignable, false otherwise
   */
  public static boolean isSubtype(Type subtype, Type supertype) {
    boolean assignable;

    if (subtype == null
        || supertype == null
        || supertype.equals(Object.class)
        || subtype == supertype) {
      /*
       * We can always assign to or from 'null', and we can always assign to Object.
       */
      assignable = true;
    } else if (subtype instanceof WildcardType) {
      /*
       * We must be able to assign from at least one of the upper bounds, including
       * the implied upper bound of Object, to the target type.
       */
      Type[] upperBounds = ((WildcardType) subtype).getUpperBounds();

      assignable = isSubtype(upperBounds, supertype);
    } else if (supertype instanceof WildcardType) {
      /*
       * If there are no lower bounds the target may be arbitrarily specific, so we
       * can never assign to it. Otherwise we must be able to assign to each lower
       * bound.
       */
      Type[] lowerBounds = ((WildcardType) supertype).getLowerBounds();

      if (lowerBounds.length == 0)
        assignable = false;
      else
        assignable = isSubtype(subtype, lowerBounds);
    } else if (subtype instanceof TypeVariable) {
      /*
       * We must be able to assign from at least one of the upper bound, including the
       * implied upper bound of Object, to the target type.
       */
      Type[] upperBounds = ((TypeVariable<?>) subtype).getBounds();

      assignable = isSubtype(upperBounds, supertype);
    } else if (supertype instanceof TypeVariable) {
      /*
       * We can only assign to a type variable if it is from the exact same type, or
       * explicitly mentioned in an upper bound or intersection type.
       */
      assignable = false;

    } else if (subtype instanceof GenericArrayType) {
      GenericArrayType fromArray = (GenericArrayType) subtype;

      if (supertype instanceof Class<?>) {
        Class<?> toClass = (Class<?>) supertype;

        assignable = toClass.isArray()
            && isSubtype(fromArray.getGenericComponentType(), toClass.getComponentType());
      } else if (supertype instanceof GenericArrayType) {
        GenericArrayType toArray = (GenericArrayType) supertype;

        assignable = isSubtype(
            fromArray.getGenericComponentType(),
            toArray.getGenericComponentType());
      } else
        assignable = false;
    } else if (supertype instanceof GenericArrayType) {
      GenericArrayType toArray = (GenericArrayType) supertype;
      if (subtype instanceof Class<?>) {
        Class<?> fromClass = (Class<?>) subtype;
        assignable = fromClass.isArray()
            && isSubtype(fromClass.getComponentType(), toArray.getGenericComponentType());
      } else
        assignable = false;
    } else if (supertype instanceof Class<?>) {
      assignable = ((Class<?>) supertype).isAssignableFrom(getErasedType(subtype));
    } else if (supertype instanceof ParameterizedType) {
      Class<?> matchedClass = getErasedType(supertype);

      if (!matchedClass.isAssignableFrom(getErasedType(subtype))) {
        assignable = false;
      } else {
        Type subtypeParameterization = resolveSupertype(subtype, matchedClass);

        if (!(subtypeParameterization instanceof ParameterizedType))
          assignable = false;
        else {
          Iterator<Type> toTypeArguments = ParameterizedTypes
              .getAllTypeArguments((ParameterizedType) supertype)
              .map(Map.Entry::getValue)
              .iterator();
          Iterator<Type> fromTypeArguments = ParameterizedTypes
              .getAllTypeArguments((ParameterizedType) subtypeParameterization)
              .map(Map.Entry::getValue)
              .iterator();

          assignable = true;
          while (toTypeArguments.hasNext()) {
            if (!isContainedBy(fromTypeArguments.next(), toTypeArguments.next()))
              assignable = false;
          }
        }
      }
    } else {
      assignable = false;
    }

    return assignable;
  }

  public static Type resolveSupertype(Type type, Class<?> superClass) {
    Type subtype = type;
    Class<?> erasedSubtype;

    while ((erasedSubtype = getErasedType(subtype)) != superClass) {
      Type genericType = concat(
          streamNullable(erasedSubtype.getGenericSuperclass()),
          Stream.of(erasedSubtype.getGenericInterfaces()))
              .filter(t -> superClass.isAssignableFrom(getErasedType(t)))
              .findFirst()
              .orElseThrow(
                  () -> new IllegalArgumentException(
                      format("Cannot find class %s on type %s", superClass, type)));

      if (subtype instanceof ParameterizedType) {
        subtype = new TypeSubstitution(
            getAllTypeArguments((ParameterizedType) subtype).collect(entriesToMap()))
                .resolve(genericType);
      } else {
        subtype = genericType;
      }
    }

    return subtype;
  }

  /**
   * Determine if a given type, {@code to}, is assignable from another given type,
   * {@code from}. Or in other words, if {@code to} is a supertype of
   * {@code from}. Types are considered assignable if they involve unchecked
   * generic casts.
   * 
   * @param from
   *          the type from which we wish to determine assignability
   * @param to
   *          the type to which we wish to determine assignability
   * @return true if the types are assignable, false otherwise
   */
  public static boolean isAssignable(Type from, Type to) {
    return isLooseInvocationContextCompatible(from, to);
  }

  /**
   * <p>
   * Determine whether a given type, {@code from}, is compatible with a given
   * type, {@code to}, within a strict invocation context.
   * 
   * <p>
   * Types are considered so compatible if assignment is possible through
   * application of the following conversions:
   * 
   * <ul>
   * <li>an identity conversion (§5.1.1)</li>
   * <li>a widening primitive conversion (§5.1.2)</li>
   * <li>a widening reference conversion (§5.1.5)</li>
   * </ul>
   * 
   * @param from
   *          The type from which to determine compatibility.
   * @param to
   *          The type to which to determine compatibility.
   * @return True if the type {@code from} is compatible with the type {@code to},
   *         false otherwise.
   */
  public static boolean isStrictInvocationContextCompatible(Type from, Type to) {
    if (isPrimitive(from)) {
      if (isPrimitive(to)) {
        if (to.equals(from) || to.equals(double.class)) {
          return true;
        } else if (to.equals(float.class)) {
          return !from.equals(double.class);
        } else if (to.equals(long.class)) {
          return !from.equals(double.class) && !from.equals(float.class);
        } else if (to.equals(int.class)) {
          return from.equals(byte.class) || from.equals(short.class) || from.equals(char.class);
        } else if (to.equals(short.class)) {
          return from.equals(byte.class);
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else if (isPrimitive(to)) {
      return false;
    }

    return isSubtype(from, to);
  }

  /**
   * <p>
   * Determine whether a given type, {@code from}, is compatible with a given
   * type, {@code to}, within a loose invocation context.
   * 
   * 
   * <p>
   * Types are considered so compatible if assignment is possible through
   * application of the following conversions:
   * 
   * <ul>
   * <li>an identity conversion</li>
   * <li>a widening primitive conversion</li>
   * <li>a widening reference conversion</li>
   * <li>a boxing conversion, optionally followed by widening reference
   * conversion</li>
   * <li>an unboxing conversion, optionally followed by a widening primitive
   * conversion</li>
   * </ul>
   * 
   * @param from
   *          The type from which to determine compatibility.
   * @param to
   *          The type to which to determine compatibility.
   * @return True if the type {@code from} is compatible with the type {@code to},
   *         false otherwise.
   */
  public static boolean isLooseInvocationContextCompatible(Type from, Type to) {
    if (from instanceof Class<?> && isGeneric((Class<?>) from)) {
      return isStrictInvocationContextCompatible(from, getErasedType(to));
    }

    if (isPrimitive(from) && !isPrimitive(to)) {
      return isStrictInvocationContextCompatible(wrapPrimitive(from), to);
    }

    if (!isPrimitive(from) && isPrimitive(to)) {
      return isStrictInvocationContextCompatible(unwrapPrimitive(from), to);
    }

    return isStrictInvocationContextCompatible(from, to);
  }
}
