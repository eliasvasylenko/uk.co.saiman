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
 * This file is part of uk.co.saiman.properties.provider.
 *
 * uk.co.saiman.properties.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties.provider;

import static java.util.Arrays.asList;
import static org.osgi.framework.Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
import static uk.co.saiman.properties.PropertyResourceBundle.getDefaultResource;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyResourceBundle;
import uk.co.saiman.properties.PropertyResourceLoader;

@Component
public class PropertyResourceLoaderService implements PropertyResourceLoader {
  @Override
  public PropertyResource loadResource(Class<?> accessorClass) {
    Bundle bundle = FrameworkUtil.getBundle(accessorClass);

    String localization = bundle.getHeaders().get(Constants.BUNDLE_LOCALIZATION);
    if (localization == null) {
      localization = BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
    }

    ClassLoader classLoader = bundle.adapt(BundleWiring.class).getClassLoader();

    return new PropertyResourceBundle(
        accessorClass,
        classLoader,
        asList(localization, getDefaultResource(accessorClass)));
  }
}
