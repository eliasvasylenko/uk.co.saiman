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

import static java.util.function.Function.identity;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;

import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.IInjector;

import uk.co.saiman.collection.StreamUtilities;

public final class EclipseUtilities {
  private EclipseUtilities() {}

  public static void injectSupertypes(IEclipseContext context, Class<?> key) {
    injectSupertypes(context, key.getName());
  }

  @SuppressWarnings("unchecked")
  public static <T> void injectSupertypes(
      IEclipseContext context,
      Class<T> key,
      Function<T, Object> mapping) {
    injectSupertypes(context, key.getName(), value -> mapping.apply((T) value));
  }

  public static void injectSupertypes(IEclipseContext context, String key) {
    injectSupertypes(context, key, identity());
  }

  public static void injectSupertypes(
      IEclipseContext context,
      String key,
      Function<Object, Object> mapping) {
    IContextFunction configurationFunction = (c, k) -> {
      Object object = c.get(key);
      if (object != null) {
        Object mappedObject = mapping.apply(object);

        try {
          Class<?> type = mappedObject.getClass().getClassLoader().loadClass(k);
          return type.isInstance(mappedObject) ? mappedObject : IInjector.NOT_A_VALUE;
        } catch (ClassNotFoundException e) {
          return IInjector.NOT_A_VALUE;
        }
      } else {
        return IInjector.NOT_A_VALUE;
      }
    };
    context.runAndTrack(new RunAndTrack() {
      @Override
      public synchronized boolean changed(IEclipseContext context) {
        Object object = context.get(key);
        if (object != null) {
          Object mappedObject = mapping.apply(object);

          runExternalCode(() -> {
            StreamUtilities
                .<Class<?>>flatMapRecursive(
                    mappedObject.getClass(),
                    t -> concat(streamNullable(t.getSuperclass()), Stream.of(t.getInterfaces())))
                .filter(c -> !c.getName().equals(key))
                .forEach(type -> context.set(type.getName(), configurationFunction));
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
