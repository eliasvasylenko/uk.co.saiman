package uk.co.saiman.eclipse.utilities;

import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.streamNullable;

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

  public static void injectSupertypes(IEclipseContext context, String key) {
    IContextFunction configurationFunction = (c, k) -> {
      Object object = c.get(key);
      if (object != null) {
        try {
          Class<?> type = object.getClass().getClassLoader().loadClass(k);
          return type.isInstance(object) ? object : IInjector.NOT_A_VALUE;
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
          runExternalCode(() -> {
            StreamUtilities
                .<Class<?>>flatMapRecursive(
                    object.getClass(),
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
