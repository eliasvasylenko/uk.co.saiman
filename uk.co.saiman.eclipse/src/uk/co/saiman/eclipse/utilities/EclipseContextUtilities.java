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

import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;

import uk.co.saiman.collection.StreamUtilities;
import uk.co.saiman.eclipse.utilities.ContextBuffer;

/**
 * General utilities for dealing with {@link IEclipseContext eclipse contexts}
 * in eclipse e4 applications.
 * 
 * @author Elias N Vasylenko
 */
public final class EclipseContextUtilities {
  private EclipseContextUtilities() {}

  /**
   * As @see #injectSubtypes(IEclipseContext, String, Class), but with the key and
   * the type both given as the same argument.
   * 
   * @param providerContext the context within which to perform the extraction and
   *                        reinjection
   * @param key             the key of the value to extract, and also the type
   *                        under which to reinject it.
   */
  public static void injectSubtypes(IEclipseContext providerContext, Class<?> key) {
    injectSubtypes(providerContext, key.getName(), key);
  }

  @SuppressWarnings("unchecked")
  public static <T, U> void injectSubtypes(
      IEclipseContext providerContext,
      Class<T> key,
      Class<U> type,
      Function<? super T, ? extends U> derivationFunction) {
    injectSubtypes(
        providerContext,
        key.getName(),
        type,
        value -> derivationFunction.apply((T) value));
  }

  /**
   * For any value registered to the given key in the given context, also inject
   * the value under all subtypes of the type which it satisfies.
   * <p>
   * Visibility and modifiability behave according to the definition of
   * {@link #injectDerived(IEclipseContext, String, BiConsumer)}.
   * 
   * @param providerContext the context within which to perform the extraction and
   *                        reinjection
   * @param key             the key of the value to extract, and also the type
   *                        under which to reinject it.
   */
  @SuppressWarnings("unchecked")
  public static <U> void injectSubtypes(
      IEclipseContext providerContext,
      String key,
      Class<U> type) {
    injectSubtypes(providerContext, key, type, t -> (U) t);
  }

  public static <U> void injectSubtypes(
      IEclipseContext providerContext,
      String key,
      Class<U> type,
      Function<? super Object, ? extends U> derivationFunction) {
    injectDerived(providerContext, key, (object, buffer) -> {
      var value = derivationFunction.apply(object);
      StreamUtilities
          .<Class<?>>flatMapRecursive(
              value.getClass(),
              t -> concat(streamNullable(t.getSuperclass()), Stream.of(t.getInterfaces())))
          .filter(type::isAssignableFrom)
          .forEach(c -> buffer.set(c.getName(), value));
    });
  }

  @SuppressWarnings("unchecked")
  public static <T> void injectDerived(
      IEclipseContext providerContext,
      Class<T> key,
      BiConsumer<? super T, ? super ContextBuffer> derivationFunction) {
    injectDerived(
        providerContext,
        key.getName(),
        (value, buffer) -> derivationFunction.accept((T) value, buffer));
  }

  /**
   * Inject derived objects based on a single source object of the given key.
   * Derived objects are only available to child contexts if the source object is
   * still visible to them, i.e. is not overridden in a more specific context.
   * 
   * @param providerContext    the root context in which to inject the derivations
   * @param key                the key of the source object
   * @param derivationFunction a mapping from the value of a source object to a
   *                           map of keys to objects to be injected
   */
  public static void injectDerived(
      IEclipseContext providerContext,
      String key,
      BiConsumer<? super Object, ? super ContextBuffer> derivationFunction) {

    providerContext.runAndTrack(new RunAndTrack() {
      @Override
      public synchronized boolean changed(IEclipseContext context) {
        Object providerObject = context.get(key);
        if (providerObject != null) {
          var derivations = ContextBuffer.empty();
          derivationFunction.accept(providerObject, derivations);

          IContextFunction configurationFunction = (requesterContext, k) -> Optional
              .ofNullable(requesterContext.get(key))
              .filter(requesterObject -> Objects.equals(requesterObject, providerObject))
              .flatMap(o -> Optional.ofNullable((Object) derivations.get(k)))
              .orElse(null);

          runExternalCode(() -> {
            derivations
                .keys()
                .filter(c -> !c.equals(key))
                .forEach(c -> context.set(c, configurationFunction));
          });
        }

        return true;
      }
    });
  }

  public static boolean isModifiable(IEclipseContext context, Class<?> key) {
    return isModifiable(context, key.getName());
  }

  public static boolean isModifiable(IEclipseContext context, String key) {
    if (!context.containsKey(key)) {
      return false;
    }

    try {
      context.modify(key, context.get(key));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
