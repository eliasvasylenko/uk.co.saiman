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

import static java.util.Collections.synchronizedMap;
import static java.util.Objects.hash;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.internal.contexts.ContextObjectSupplier;
import org.eclipse.e4.core.internal.di.Requestor;
import org.osgi.service.component.annotations.Deactivate;

import uk.co.saiman.properties.PropertyLoader;

/**
 * Supplier for Eclipse DI contexts, to provide localization implementations of
 * a requested type via a {@link PropertyLoader}.
 *
 * @since 1.2
 */
public abstract class TransformingNamedObjectSupplier<T extends Annotation>
    extends ExtendedObjectSupplier {
  protected abstract class Request {
    private final T qualifier;

    private final IRequestor requestor;
    private final IEclipseContext context;

    public Request(IObjectDescriptor descriptor, IRequestor requestor) {
      this.qualifier = descriptor.getQualifier(qualifierClass);

      this.requestor = requestor;
      this.context = ((ContextObjectSupplier) ((Requestor<?>) requestor).getPrimarySupplier())
          .getContext();
    }

    public IEclipseContext getContext() {
      return context;
    }

    public T getQualifier() {
      return qualifier;
    }

    public Tracker getTracker() {
      return trackers.computeIfAbsent(this, Tracker::new);
    }

    public void disposeTracker() {
      Tracker tracker = trackers.remove(this);
      tracker.dispose();
    }

    public Object get() {
      Object object = get(context);
      return object != null ? object : IInjector.NOT_A_VALUE;
    }

    protected abstract Object get(IEclipseContext context);

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!(obj instanceof TransformingNamedObjectSupplier<?>.Request))
        return false;

      @SuppressWarnings("unchecked")
      Request that = (Request) obj;

      return Objects.equals(this.qualifier, that.qualifier)
          && Objects.equals(this.requestor, that.requestor);
    }

    @Override
    public int hashCode() {
      return hash(qualifier, requestor);
    }
  }

  class Tracker {
    private final Request request;
    private boolean disposed = false;
    private Object namedObject;

    public Tracker(Request request) {
      request.getContext().runAndTrack(new RunAndTrack() {
        @Override
        public boolean changed(IEclipseContext context) {
          if (disposed) {
            return false;
          }

          Object namedObject = request.get(context);

          runExternalCode(() -> {
            if (!Objects.equals(Tracker.this.namedObject, namedObject)) {
              Tracker.this.namedObject = namedObject;

              // if this is not the first time ...
              if (Tracker.this.request != null) {
                request.requestor.resolveArguments(false);
                request.requestor.execute();
              }
            }
          });

          return true;
        }
      });
      this.request = request;
    }

    public void dispose() {
      disposed = true;
    }

    public Object get() {
      return namedObject != null ? namedObject : IInjector.NOT_A_VALUE;
    }
  }

  private final Class<T> qualifierClass;

  public TransformingNamedObjectSupplier(Class<T> qualifierClass) {
    this.qualifierClass = qualifierClass;
  }

  private final Map<Request, Tracker> trackers = synchronizedMap(new HashMap<>());

  @Override
  public Object get(
      IObjectDescriptor descriptor,
      IRequestor requestor,
      boolean track,
      boolean group) {
    Request request = getRequest(descriptor, requestor);

    if (!requestor.isValid()) {
      request.disposeTracker();
      return IInjector.NOT_A_VALUE;
    }

    return track ? request.getTracker().get() : request.get();
  }

  protected abstract Request getRequest(IObjectDescriptor descriptor, IRequestor requestor);

  @Deactivate
  public void dispose() {
    trackers.values().forEach(Tracker::dispose);
  }
}
