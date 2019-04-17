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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.service;

import static java.lang.String.format;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import uk.co.saiman.fx.FxUtilities;
import uk.co.saiman.osgi.ServiceWiringException;

@Component(
    service = ExtendedObjectSupplier.class,
    property = "dependency.injection.annotation:String=uk.co.saiman.eclipse.service.ObservableService")
public class ObservableServiceSupplier extends ExtendedObjectSupplier {
  private class ServiceUpdateListener<T> implements ServiceListener {
    private final BundleContext context;
    private final ObservableList<ServiceReference<T>> references;
    private final Map<ServiceReference<T>, T> serviceObjects;
    private final Class<T> elementType;
    private final String filter;

    @SuppressWarnings("unchecked")
    public ServiceUpdateListener(
        BundleContext context,
        Type elementType,
        ObservableService annotation) throws InvalidSyntaxException {
      this.context = context;
      this.references = FXCollections.observableArrayList();
      this.elementType = elementType instanceof ParameterizedType
          ? (Class<T>) ((ParameterizedType) elementType).getRawType()
          : (Class<T>) elementType;
      this.serviceObjects = new HashMap<>();

      synchronized (this) {
        String filter = "(" + Constants.OBJECTCLASS + "=" + this.elementType.getName() + ")";

        if (!annotation.target().equals("")) {
          filter = "(&" + annotation.target() + filter + ")";
        }

        this.filter = filter;

        context.addServiceListener(this, filter);

        refreshServices();
      }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
      refreshServices();
    }

    private synchronized void refreshServices() {
      try {
        List<ServiceReference<T>> newReferences = new ArrayList<>(
            context.getServiceReferences(elementType, filter));
        Collections.sort(newReferences);

        for (Iterator<ServiceReference<T>> services = references.iterator(); services.hasNext();) {
          ServiceReference<T> service = services.next();

          if (!newReferences.contains(service)) {
            services.remove();
            context.getServiceObjects(service).ungetService(serviceObjects.remove(service));
          }
        }

        int index = 0;
        for (ServiceReference<T> newReference : newReferences) {
          if (!references.contains(newReference)) {
            references.add(index, newReference);
            serviceObjects.put(newReference, context.getServiceObjects(newReference).getService());
          }
          index++;
        }
      } catch (InvalidSyntaxException e) {
        throw new AssertionError();
      }
    }

    public ObservableList<T> getServiceList() {
      return FxUtilities.map(references.sorted(), serviceObjects::get).filtered(t -> t != null);
    }

    public ObservableSet<T> getServiceSet() {
      return FxUtilities.asSet(getServiceList());
    }

    public ObservableValue<T> getServiceValue() {
      SimpleObjectProperty<T> value = new SimpleObjectProperty<>();

      references.addListener((ListChangeListener<ServiceReference<T>>) c -> {
        value.set(context.getService(references.get(0)));
      });

      return value;
    }
  }

  @Override
  public Object get(
      IObjectDescriptor descriptor,
      IRequestor requestor,
      boolean track,
      boolean group) {
    try {
      Type collectionType = descriptor.getDesiredType();
      Bundle bundle = FrameworkUtil.getBundle(requestor.getRequestingObjectClass());

      if (collectionType instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) collectionType;

        ServiceUpdateListener<?> listener = new ServiceUpdateListener<>(
            bundle.getBundleContext(),
            parameterizedType.getActualTypeArguments()[0],
            descriptor.getQualifier(ObservableService.class));

        if (parameterizedType.getRawType() == ObservableList.class) {
          return listener.getServiceList();
        }
        if (parameterizedType.getRawType() == ObservableSet.class) {
          return listener.getServiceSet();
        }
        if (parameterizedType.getRawType() == ObservableValue.class) {
          return listener.getServiceValue();
        }
      }

      throw new ServiceWiringException(
          format(
              "Illegal %s injection target %s",
              ObservableService.class.getTypeName(),
              descriptor.getDesiredType().getTypeName()));
    } catch (ServiceWiringException e) {
      throw e;
    } catch (Exception e) {
      throw new ServiceWiringException(
          "An unexpected problem was encountered attempting to provide an observable service supplier",
          e);
    }
  }
}
