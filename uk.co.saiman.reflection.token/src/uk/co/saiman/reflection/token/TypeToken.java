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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.empty;
import static uk.co.saiman.collection.StreamUtilities.zip;
import static uk.co.saiman.reflection.ReflectionException.REFLECTION_PROPERTIES;
import static uk.co.saiman.reflection.token.TypeParameter.forTypeVariable;

import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.reflection.ParameterizedTypes;
import uk.co.saiman.reflection.ReflectionException;
import uk.co.saiman.reflection.TypeHierarchy;
import uk.co.saiman.reflection.TypeSubstitution;
import uk.co.saiman.reflection.Types;

/**
 * <p>
 * TypeToken provides reflective operations and services over the Java type
 * system. It is analogous to {@code Class<?>}, but provides access to a much
 * richer set of tools, and can be used over the domain of all types, not just
 * raw types.
 * 
 * <p>
 * TypeToken is effectively immutable, though may perform shared caching of
 * results transparently to the user. Like Class, A TypeToken will always be
 * parameterized with the type it reflects over when used as intended.
 * 
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          This is the type which the TypeToken object references.
 */
public class TypeToken<T> {
  private final Type type;

  protected TypeToken() {
    this.type = resolveSuperclassParameter();
  }

  protected TypeToken(Type type) {
    this.type = type;
  }

  private Type resolveSuperclassParameter() {
    Class<?> subclass = getClass();

    Type type;

    final Map<TypeVariable<?>, Type> resolvedParameters = new HashMap<>();
    final TypeSubstitution resolvedParameterSubstitution = new TypeSubstitution()
        .where(t -> t instanceof TypeVariable, t -> resolvedParameters.get(t));

    do {
      Type annotatedType = subclass.getGenericSuperclass();

      if (annotatedType instanceof ParameterizedType) {
        if (!resolvedParameters.isEmpty()) {
          type = resolvedParameterSubstitution.resolve(annotatedType);
        } else
          type = annotatedType;

        resolvedParameters.clear();
        ParameterizedTypes.getAllTypeArguments((ParameterizedType) type).forEach(
            e -> resolvedParameters.put(e.getKey(), e.getValue()));
      } else {
        type = annotatedType;

        resolvedParameters.clear();
      }

      subclass = subclass.getSuperclass();
    } while (!subclass.equals(TypeToken.class));

    return resolvedParameters.values().iterator().next();
  }

  /**
   * Create a TypeToken for an arbitrary type, preserving wildcards where
   * possible.
   * 
   * @param type
   *          the requested type
   * @return a TypeToken over the requested type
   */
  public static TypeToken<?> forType(Type type) {
    return new TypeToken<>(type);
  }

  /**
   * Create a TypeToken for an arbitrary type, preserving wildcards where
   * possible.
   * 
   * @param type
   *          the requested type
   * @return a TypeToken over the requested type
   */
  public static <T> TypeToken<T> forType(Class<T> type) {
    return new TypeToken<>(type);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof TypeToken && getType().equals(((TypeToken<?>) obj).getType());
  }

  @Override
  public int hashCode() {
    return getType().hashCode();
  }

  /**
   * See {@link Types#getErasedType(Type)}.
   * 
   * @return the raw type of the type represented by this TypeToken
   */
  @SuppressWarnings("unchecked")
  public Class<? super T> getErasedType() {
    return (Class<? super T>) getErasedUpperBounds().findFirst().orElse(Object.class);
  }

  /**
   * See {@link Types#getErasedType(Type)}.
   * 
   * @return the raw type of the type represented by this TypeToken
   */
  @SuppressWarnings("unchecked")
  public TypeToken<? super T> getErasedTypeToken() {
    if (isRaw()) {
      return this;
    } else {
      return (TypeToken<? super T>) forType(
          getErasedUpperBounds().findFirst().orElse(Object.class));
    }
  }

  /**
   * Find the upper bounding classes and parameterized types of a given type.
   * Unlike {@link Types#getUpperBounds(Type)} this respects bounds on the
   * inference variables in this resolver.
   * 
   * @return the upper bounds of the type represented by this TypeToken
   */
  public Stream<Type> getUpperBounds() {
    List<Type> upperBounds = Types.getUpperBounds(getType()).collect(toList());

    if (upperBounds.isEmpty())
      upperBounds.add(Object.class);

    return upperBounds.stream();
  }

  /**
   * Determine the raw types of a given type, accounting for inference variables
   * which may have instantiations or upper bounds within the context of this
   * resolver.
   * 
   * @return the raw types of the type represented by this TypeToken
   */
  public Stream<Class<?>> getErasedUpperBounds() {
    return getUpperBounds().map(Types::getErasedType);
  }

  @Override
  public String toString() {
    return type.toString();
  }

  /**
   * The type represented by this {@link TypeToken}.
   * 
   * @return The actual Type object described.
   */
  public Type getType() {
    return type;
  }

  /**
   * Is the type a primitive type as per the Java type system.
   * 
   * @return True if the type is primitive, false otherwise.
   */
  public boolean isPrimitive() {
    return Types.isPrimitive(getType());
  }

  /**
   * Is the type a wrapper for a primitive type as per the Java type system.
   * 
   * @return True if the type is a primitive wrapper, false otherwise.
   */
  public boolean isPrimitiveWrapper() {
    return Types.isPrimitiveWrapper(getType());
  }

  /**
   * If this TypeToken is a primitive type, determine the wrapped primitive
   * type.
   * 
   * @return The wrapper type of the primitive type this TypeToken represents,
   *         otherwise this TypeToken itself.
   */
  @SuppressWarnings("unchecked")
  public TypeToken<T> wrapPrimitive() {
    if (isPrimitive())
      return (TypeToken<T>) forType(Types.wrapPrimitive(getErasedType()));
    else
      return this;
  }

  /**
   * If this TypeToken is a wrapper of a primitive type, determine the unwrapped
   * primitive type.
   * 
   * @return The primitive type wrapped by this TypeToken, otherwise this
   *         TypeToken itself.
   */
  @SuppressWarnings("unchecked")
  public TypeToken<T> unwrapPrimitive() {
    if (isPrimitiveWrapper())
      return (TypeToken<T>) forType(Types.unwrapPrimitive(getErasedType()));
    else
      return this;
  }

  /**
   * If the declaration is raw, parameterize it with its own type parameters,
   * otherwise return the declaration itself.
   * 
   * @return the parameterized version of the declaration where applicable, else
   *         the unmodified declaration
   */
  @SuppressWarnings("unchecked")
  public TypeToken<? extends T> parameterize() {
    if (isRaw()) {
      return (TypeToken<T>) forType(ParameterizedTypes.parameterize(getErasedType()));
    } else {
      return this;
    }
  }

  /**
   * @return true if the declaration represents a raw type or invocation, false
   *         otherwise
   */
  public boolean isRaw() {
    return getType() instanceof Class<?> && ((Class<?>) getType()).getTypeParameters().length > 0;
  }

  /**
   * @return true if the declaration is generic, false otherwise
   */
  public boolean isGeneric() {
    return isRaw() || getType() instanceof ParameterizedType;
  }

  /**
   * @return the count of the generic type parameters of the declaration.
   */
  public int getTypeParameterCount() {
    if (getType() instanceof ParameterizedType) {
      return ((ParameterizedType) getType()).getActualTypeArguments().length;
    } else {
      return 0;
    }
  }

  /**
   * @return The generic type parameter instantiations of the wrapped
   *         {@link Executable}, or their inference variables if not yet
   *         instantiated.
   */
  public Stream<TypeParameter<?>> getTypeParameters() {
    if (getType() instanceof ParameterizedType) {
      return Arrays.stream(getErasedType().getTypeParameters()).map(e -> forTypeVariable(e));
    } else {
      return Stream.empty();
    }
  }

  /**
   * @return The generic type parameter instantiations of the wrapped
   *         {@link Executable}, or their inference variables if not yet
   *         instantiated.
   */
  public Stream<TypeArgument<?>> getTypeArguments() {
    if (getType() instanceof ParameterizedType) {

      Stream<TypeVariable<?>> parameters = Arrays.stream(getErasedType().getTypeParameters());
      Stream<Type> arguments = Arrays
          .stream(((ParameterizedType) getType()).getActualTypeArguments());

      return zip(parameters, arguments).map(e -> forTypeVariable(e.getKey()).asType(e.getValue()));
    } else {
      return Stream.empty();
    }
  }

  /**
   * Derive a new {@link TypeToken} instance from this, with types substituted
   * according to the given arguments.
   * 
   * <p>
   * More specifically, each of the given arguments represents a type variable
   * and an instantiation for that type variable. Occurrences of those type
   * variables in the declaration will be substituted for their instantiations
   * in the derived declaration.
   * 
   * <p>
   * The substitution will only succeed if it results in a valid
   * parameterization of the declaration.
   * 
   * <p>
   * For example, the following method could be used to derive instances of
   * TypeToken over different parameterizations of {@code List<?>} at runtime.
   * 
   * <pre>
   * <code>
   * public TypeToken&lt;List&lt;T&gt;&gt; getListType(TypeToken&lt;T&gt; elementType)} {
   *   return new TypeToken&lt;T&gt;()} {}.withTypeArguments(new TypeParameter&lt;T&gt;() {}.as(elementType));
   * }
   * </code>
   * </pre>
   * 
   * @param arguments
   *          the type variable instantiations
   * @return a new derived {@link TypeToken} instance with the given
   *         instantiation substituted for the given type variable
   */
  public TypeToken<T> withTypeArguments(Collection<? extends TypeArgument<?>> arguments) {
    Map<TypeVariable<?>, Type> argumentMap = arguments.stream().collect(
        toMap(a -> a.getParameter().getType(), TypeArgument::getType));

    return new TypeToken<>(new TypeSubstitution(argumentMap).resolve(getType()));
  }

  /**
   * @return the declaration directly enclosing this declaration
   */
  public Optional<TypeToken<?>> getOwningType() {
    if (getType() instanceof ParameterizedType) {
      ParameterizedType type = (ParameterizedType) getType();
      Class<?> rawType = (Class<?>) type.getRawType();

      if (rawType.getEnclosingClass() != null) {
        return Optional.of(new TypeToken<>(type.getOwnerType()));

      } else {
        return Optional.empty();
      }

    } else if (getType() instanceof Class<?>) {
      Class<?> type = (Class<?>) getType();
      if (type.getEnclosingClass() != null) {
        return Optional.of(new TypeToken<>(type.getEnclosingClass()));

      } else {
        return Optional.empty();
      }

    } else if (getType() instanceof TypeVariable<?>) {
      GenericDeclaration enclosingDeclaration = ((TypeVariable<?>) getType())
          .getGenericDeclaration();

      if (enclosingDeclaration instanceof Class<?>) {
        Class<?> enclosingClass = (Class<?>) enclosingDeclaration;
        return Optional.of(
            forType(
                Types.isGeneric(enclosingClass)
                    ? ParameterizedTypes.parameterize(enclosingClass)
                    : enclosingClass));

      } else {
        return Optional.empty();
      }

    } else {
      return Optional.empty();
    }
  }

  /**
   * As @see {@link TypeHierarchy#resolveSupertype( Class)}.
   */
  @SuppressWarnings({ "unchecked", "javadoc" })
  public TypeToken<? super T> resolveSupertype(Class<?> superclass) {
    TypeToken<?> superType = forType(new TypeHierarchy(getType()).resolveSupertype(superclass));
    return (TypeToken<? super T>) superType;
  }

  /**
   * As @see {@link #withAllTypeArguments(List)}, but only providing arguments
   * for the parameters occurring directly on the declaration.
   */
  public TypeToken<T> withTypeArguments(List<Type> typeArguments) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Derive a new {@link TypeToken} instance with the given type argument
   * parameterization.
   * 
   * <p>
   * The types in the given list correspond, in order, to the
   * {@link #getTypeParameters() type parameters} of this declaration. The
   * current parameterization of the declaration is substituted for that given.
   * 
   * <p>
   * This behavior is different from {@link #withTypeArguments(Collection)},
   * which performs a substitution for arbitrary type variables rather than
   * re-instantiating every parameter on the declaration.
   * 
   * @param typeArguments
   *          a list of arguments for each generic type parameter of the
   *          underlying declaration
   * @return a new derived {@link TypeToken} instance with the given
   *         instantiations substituted for each generic type parameter, in
   *         order
   */
  public TypeToken<T> withAllTypeArguments(List<Type> typeArguments) {
    // TODO Auto-generated method stub
    return null;
  }

  public TypeToken<T> withTypeArguments(Type... typeArguments) {
    return withTypeArguments(asList(typeArguments));
  }

  public TypeToken<T> withAllTypeArguments(Type... typeArguments) {
    return withAllTypeArguments(asList(typeArguments));
  }

  /**
   * @return the count of all generic type parameters of the declaration and any
   *         enclosing declarations
   */
  public int getAllTypeParameterCount() {
    return getTypeParameterCount()
        + getOwningType().map(TypeToken::getAllTypeParameterCount).orElse(0);
  }

  /**
   * @return all generic type parameters of the declaration and any enclosing
   *         declarations
   */
  public Stream<TypeParameter<?>> getAllTypeParameters() {
    return Stream.concat(
        getTypeParameters(),
        getOwningType().map(TypeToken::getAllTypeParameters).orElse(empty()));
  }

  /**
   * @return all generic type parameter instantiations of the declaration, or
   *         their inference variables if not yet instantiated.
   */
  public Stream<TypeArgument<?>> getAllTypeArguments() {
    if (isRaw())
      return Stream.empty();
    else
      return Stream.concat(
          getTypeArguments(),
          getOwningType().map(TypeToken::getAllTypeArguments).orElse(empty()));
  }

  /**
   * Derive a new {@link TypeToken} instance from this, with the given
   * instantiation substituted for the given {@link TypeVariable}.
   * 
   * <p>
   * For example, the following method could be used to derive instances of
   * TypeToken over different parameterizations of {@code List<?>} at runtime.
   * 
   * <pre>
   * <code>
   * public TypeToken&lt;List&lt;T&gt;&gt; getListType(TypeToken&lt;T&gt; elementType)} {
   *   return new TypeToken&lt;T&gt;()} {}.withTypeArguments(new TypeParameter&lt;T&gt;() {}.as(elementType));
   * }
   * </code>
   * </pre>
   * 
   * <p>
   * This behavior is different from {@link #withTypeArguments(List)}, which
   * re-instantiates every parameter on the declaration rather than performing a
   * substitution for arbitrary type variables.
   * 
   * @param arguments
   *          the type variable instantiations
   * @return a new derived {@link TypeToken} instance with the given
   *         instantiation substituted for the given type variable
   */
  public TypeToken<T> withTypeArguments(TypeArgument<?>... arguments) {
    return withTypeArguments(Arrays.asList(arguments));
  }

  /**
   * Resolve the instantiation of the given type variable in the context of this
   * declaration.
   * 
   * @param <U>
   *          the type of the type variable to resolve
   * @param parameter
   *          the type parameter
   * @return the argument of the given parameter with respect to this
   *         declaration
   */
  @SuppressWarnings("unchecked")
  public <U> TypeArgument<U> resolveTypeArgument(TypeParameter<U> parameter) {
    return getAllTypeArguments()
        .filter(a -> a.getParameter().getType().equals(parameter.getType()))
        .findAny()
        .map(p -> (TypeArgument<U>) p)
        .orElseThrow(
            () -> new ReflectionException(
                REFLECTION_PROPERTIES.cannotResolveTypeVariable(parameter.getType(), this)));
  }

  /**
   * @see #resolveTypeArgument(TypeParameter)
   */
  @SuppressWarnings("javadoc")
  public Type resolveTypeArgument(TypeVariable<?> parameter) {
    return resolveTypeArgument(forTypeVariable(parameter)).getType();
  }

  public boolean isAssignableTo(Type type) {
    return Types.isAssignable(this.type, type);
  }

  public boolean isAssignableTo(TypeToken<?> type) {
    return Types.isAssignable(this.type, type.type);
  }

  public boolean isAssignableFrom(Type type) {
    return Types.isAssignable(type, this.type);
  }

  public boolean isAssignableFrom(TypeToken<?> type) {
    return Types.isAssignable(type.type, this.type);
  }

}
