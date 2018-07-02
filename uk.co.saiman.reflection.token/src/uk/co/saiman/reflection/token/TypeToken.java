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

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.empty;
import static uk.co.saiman.collection.StreamUtilities.zip;
import static uk.co.saiman.reflection.token.TypeParameter.forTypeVariable;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.reflection.ParameterizedTypes;
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
  @SuppressWarnings("unused")
  private static final Object ANNOTATED_OBJECT_FIELD = null;
  private static final AnnotatedType ANNOTATED_OBJECT_TYPE;
  static {
    try {
      ANNOTATED_OBJECT_TYPE = TypeToken.class
          .getDeclaredField("ANNOTATED_OBJECT_FIELD")
          .getAnnotatedType();
    } catch (NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  private final Type type;

  protected TypeToken() {
    AnnotatedType annotatedType = resolveSuperclassParameter();
    this.type = annotatedType.getType();

  }

  protected TypeToken(Type type) {
    this.type = type;
  }

  protected AnnotatedType resolveSuperclassParameter() {
    Class<?> subclass = getClass();

    for (;;) {
      Class<?> superclass = subclass.getSuperclass();

      if (superclass.equals(TypeToken.class))
        break;

      subclass = superclass;
    }

    AnnotatedType superType = subclass.getAnnotatedSuperclass();
    if (superType instanceof AnnotatedParameterizedType) {
      return ((AnnotatedParameterizedType) superType).getAnnotatedActualTypeArguments()[0];
    } else {
      return ANNOTATED_OBJECT_TYPE;
    }
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
    return (Class<? super T>) Types.getErasedType(type);
  }

  /**
   * See {@link Types#getErasedType(Type)}.
   * 
   * @return the raw type of the type represented by this TypeToken
   */
  @SuppressWarnings("unchecked")
  public TypeToken<? super T> getErasedTypeToken() {
    return (TypeToken<? super T>) forType(Types.getErasedType(type));
  }

  @Override
  public String toString() {
    return type.getTypeName();
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
        return Optional
            .of(
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
    TypeToken<?> superType = forType(Types.resolveSupertype(getType(), superclass));
    return (TypeToken<? super T>) superType;
  }

  /**
   * @return all generic type parameters of the declaration and any enclosing
   *         declarations
   */
  public Stream<TypeParameter<?>> getAllTypeParameters() {
    return Stream
        .concat(
            getTypeParameters(),
            getOwningType().map(TypeToken::getAllTypeParameters).orElse(empty()));
  }

  /**
   * @return all generic type parameter instantiations of the declaration, or
   *         their inference variables if not yet instantiated.
   */
  public Stream<TypeArgument<?>> getAllTypeArguments() {
    if (Types.isErasure(type))
      return Stream.empty();
    else
      return Stream
          .concat(
              getTypeArguments(),
              getOwningType().map(TypeToken::getAllTypeArguments).orElse(empty()));
  }

  /**
   * Derive a new {@link TypeToken} instance from this, with types substituted
   * according to the given arguments.
   * 
   * <p>
   * More specifically, each of the given arguments represents a type variable and
   * an instantiation for that type variable. Occurrences of those type variables
   * in the declaration will be substituted for their instantiations in the
   * derived declaration.
   * 
   * <p>
   * The substitution will only succeed if it results in a valid parameterization
   * of the declaration.
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
   * @return a new derived {@link TypeToken} instance with the given instantiation
   *         substituted for the given type variable
   */
  public TypeToken<T> withTypeArguments(Collection<? extends TypeArgument<?>> arguments) {
    Map<TypeVariable<?>, Type> argumentMap = arguments
        .stream()
        .collect(toMap(a -> a.getParameter().getType(), TypeArgument::getType));

    return new TypeToken<>(new TypeSubstitution(argumentMap).resolve(getType()));
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
   * This behavior is different from {@link #withTypeArguments(Collection)}, which
   * re-instantiates every parameter on the declaration rather than performing a
   * substitution for arbitrary type variables.
   * 
   * @param arguments
   *          the type variable instantiations
   * @return a new derived {@link TypeToken} instance with the given instantiation
   *         substituted for the given type variable
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
   * @return the argument of the given parameter with respect to this declaration
   */
  @SuppressWarnings("unchecked")
  public <U> TypeArgument<U> resolveTypeArgument(TypeParameter<U> parameter) {
    return getAllTypeArguments()
        .filter(a -> a.getParameter().getType().equals(parameter.getType()))
        .findAny()
        .map(p -> (TypeArgument<U>) p)
        .orElseThrow(
            () -> new IllegalArgumentException(
                "Cannot find parameter " + parameter.getType() + " on type " + type));
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
