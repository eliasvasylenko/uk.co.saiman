package uk.co.saiman.eclipse.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;
import org.eclipse.e4.core.internal.di.MethodRequestor;

public class StaticMethodRequestor extends MethodRequestor {
  private final Consumer<Object> callback;

  public StaticMethodRequestor(
      Method method,
      IInjector injector,
      PrimaryObjectSupplier primarySupplier,
      PrimaryObjectSupplier tempSupplier,
      Object requestingObject,
      Consumer<Object> callback) {
    super(method, injector, primarySupplier, tempSupplier, requestingObject, false);
    this.callback = callback;
  }

  @SuppressWarnings("deprecation")
  @Override
  public Object execute() throws InjectionException {
    if (actualArgs == null) {
      if (location.getParameterTypes().length > 0)
        return null; // optional method call
    }
    Object result = null;
    if (!location.isAccessible()) {
      location.setAccessible(true);
    }
    boolean pausedRecording = false;
    if ((primarySupplier != null)) {
      primarySupplier.pauseRecording();
      pausedRecording = true;
    }
    try {
      result = location.invoke(null, actualArgs);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new InjectionException(e);
    } catch (InvocationTargetException e) {
      Throwable originalException = e.getCause();
      // Errors such as ThreadDeath or OutOfMemoryError should not be trapped
      // http://bugs.eclipse.org/bugs/show_bug.cgi?id=457687
      if (originalException instanceof Error) {
        throw (Error) originalException;
      }
      throw new InjectionException((originalException != null) ? originalException : e);
    } finally {
      if (pausedRecording)
        primarySupplier.resumeRecording();
      clearResolvedArgs();
    }

    callback.accept(result);
    return result;
  }
}
