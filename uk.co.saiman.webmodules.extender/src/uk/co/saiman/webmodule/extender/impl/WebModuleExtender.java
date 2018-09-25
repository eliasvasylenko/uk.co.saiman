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
 * This file is part of uk.co.saiman.webmodules.extender.
 *
 * uk.co.saiman.webmodules.extender is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.extender is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.extender.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.Bundle;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.osgi.ExtenderManager;
import uk.co.saiman.webmodule.extender.WebModuleExtenderConstants;

@Capability(
    namespace = ExtenderNamespace.EXTENDER_NAMESPACE,
    name = WebModuleExtenderConstants.EXTENDER_NAME,
    version = WebModuleExtenderConstants.EXTENDER_VERSION)
@Component(immediate = true)
public class WebModuleExtender extends ExtenderManager {
  private final Map<Bundle, WebModuleCapabilities> bundleCapabilities = new HashMap<>();

  @Activate
  public WebModuleExtender(ComponentContext context) {
    super(context);
  }

  @Override
  protected synchronized boolean register(Bundle bundle) {
    bundleCapabilities.put(bundle, new WebModuleCapabilities(bundle));
    return true;
  }

  @Override
  protected synchronized void update(Bundle bundle) {
    bundleCapabilities.get(bundle).update();
  }

  @Override
  protected synchronized void unregister(Bundle bundle) {
    bundleCapabilities.remove(bundle).dispose();
  }
}
