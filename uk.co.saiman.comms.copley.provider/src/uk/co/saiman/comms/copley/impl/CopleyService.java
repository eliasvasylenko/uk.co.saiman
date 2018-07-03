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
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.copley.CopleyController;

@Component(service = CopleyService.class)
public class CopleyService {
  private final Map<CopleyController, ServiceReference<CopleyController>> controllers = new HashMap<>();;

  public CopleyService() {}

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC, unbind = "removeController")
  synchronized void addController(
      CopleyController controller,
      ServiceReference<CopleyController> serviceReference,
      Map<String, ?> properties) {
    if (controllers.containsKey(controller)) {
      throw new IllegalArgumentException("Already contains controller " + controller);
    }
    String servicePid = (String) serviceReference.getProperty(SERVICE_PID);
    if (servicePid != null) {
      controllers.put(controller, serviceReference);
    }
  }

  synchronized void removeController(
      CopleyController controller,
      ServiceReference<CopleyController> serviceReference) {
    controllers.remove(controller, serviceReference);
  }

  public String getId(CopleyController controller) {
    return (String) controllers.get(controller).getProperty(SERVICE_PID);
  }

  public CopleyController getController(String id) {
    return controllers
        .keySet()
        .stream()
        .filter(c -> getId(c).equals(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find controller " + id));
  }

  public Stream<CopleyController> getControllers() {
    return controllers.keySet().stream();
  }

  public Bundle getBundle(CopleyController controller) {
    return controllers.get(controller).getBundle();
  }
}
