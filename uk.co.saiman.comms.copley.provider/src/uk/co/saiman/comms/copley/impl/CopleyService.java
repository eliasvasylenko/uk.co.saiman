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
 * This file is part of uk.co.saiman.comms.copley.provider.
 *
 * uk.co.saiman.comms.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.copley.CopleyController;

@Component(service = CopleyService.class)
public class CopleyService {
  private final Map<String, CopleyController> controllers = new HashMap<>();
  private final Map<CopleyController, String> ids = new HashMap<>();
  private final Map<CopleyController, Bundle> bundles = new HashMap<>();

  public CopleyService() {}

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC, unbind = "removeController")
  synchronized void addController(
      CopleyController controller,
      ServiceReference<CopleyController> serviceReference) {
    String servicePid = (String) serviceReference.getProperty(SERVICE_PID);
    if (servicePid != null) {
      controllers.put(servicePid, controller);
      ids.put(controller, servicePid);
      bundles.put(controller, serviceReference.getBundle());
    }
  }

  synchronized void removeController(CopleyController controller) {
    controllers.remove(ids.remove(controller));
    bundles.remove(controller);
  }

  public String getId(CopleyController controller) {
    return Optional
        .ofNullable(ids.get(controller))
        .orElseThrow(
            () -> new IllegalArgumentException("Cannot find if for controller " + controller));
  }

  public CopleyController getController(String id) {
    return Optional
        .ofNullable(controllers.get(id))
        .orElseThrow(() -> new IllegalArgumentException("Cannot find controller for id " + id));
  }

  public Stream<String> getIds() {
    return controllers.keySet().stream();
  }

  public Stream<CopleyController> getControllers() {
    return controllers.values().stream();
  }

  public Bundle getBundle(CopleyController controller) {
    return bundles.get(controller);
  }
}
