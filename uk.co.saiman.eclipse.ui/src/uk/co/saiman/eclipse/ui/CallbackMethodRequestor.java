package uk.co.saiman.eclipse.ui;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;
import org.eclipse.e4.core.internal.di.MethodRequestor;

public class CallbackMethodRequestor extends MethodRequestor {
  private final Consumer<Object> callback;

  public CallbackMethodRequestor(
      Method method,
      IInjector injector,
      PrimaryObjectSupplier primarySupplier,
      PrimaryObjectSupplier tempSupplier,
      Object requestingObject,
      boolean track,
      Consumer<Object> callback) {
    super(method, injector, primarySupplier, tempSupplier, requestingObject, track);
    this.callback = callback;
  }

  @Override
  public Object execute() throws InjectionException {
    Object result = super.execute();
    callback.accept(result);
    return result;
  }
}
