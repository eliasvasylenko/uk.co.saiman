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
package uk.co.saiman.eclipse.localization;

import static java.lang.String.format;

import java.lang.reflect.Type;

import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.properties.PropertyLoader;
import uk.co.saiman.properties.PropertyLoaderException;

/**
 * Supplier for Eclipse DI contexts, to provide localization implementations of
 * a requested type via a {@link PropertyLoader}.
 *
 * @since 1.2
 */
@Component(
    service = ExtendedObjectSupplier.class,
    property = "dependency.injection.annotation:String=uk.co.saiman.eclipse.localization.Localize",
    immediate = true)
public class LocalizationSupplier extends ExtendedObjectSupplier {
  @Reference
  PropertyLoader generalLocalizer;

  @Override
  public Object get(
      IObjectDescriptor descriptor,
      IRequestor requestor,
      boolean track,
      boolean group) {
    try {
      Type accessor = descriptor.getDesiredType();

      if (validateAccessorType(accessor)) {
        return localizeAccessor(requestor, (Class<?>) accessor);

      } else {
        throw new PropertyLoaderException(
            format(
                "Illegal %s injection target %s must be an interface",
                Localize.class.getTypeName(),
                accessor.getTypeName()));
      }
    } catch (PropertyLoaderException e) {
      throw e;
    } catch (Exception e) {
      throw new PropertyLoaderException(
          "An unexpected problem was encountered providing a localized text service",
          e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Object localizeAccessor(IRequestor requestor, Class<?> accessor) {
    try {
      BundleContext context = FrameworkUtil.getBundle(accessor).getBundleContext();

      ServiceReference<PropertyLoader> localizerServiceRererence = context
          .getServiceReference(PropertyLoader.class);

      PropertyLoader localizer = localizerServiceRererence != null
          ? context.getService(localizerServiceRererence)
          : generalLocalizer;

      T localization = localizer.getProperties((Class<T>) accessor);

      context.addServiceListener(new ServiceListener() {
        @Override
        public void serviceChanged(ServiceEvent event) {
          if (event.getType() == ServiceEvent.UNREGISTERING
              && event.getServiceReference().equals(localizerServiceRererence)) {
            try {
              requestor.resolveArguments(false);
              requestor.execute();
            } catch (Exception e) {}
          }
        }
      });

      return localization;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  private boolean validateAccessorType(Type accessor) {
    return accessor instanceof Class && ((Class<?>) accessor).isInterface();
  }
}
