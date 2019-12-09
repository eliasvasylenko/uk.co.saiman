/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.ui.
 *
 * uk.co.saiman.eclipse.ui is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
