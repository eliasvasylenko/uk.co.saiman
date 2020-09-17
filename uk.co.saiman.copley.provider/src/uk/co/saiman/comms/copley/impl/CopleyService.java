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
 * This file is part of uk.co.saiman.copley.provider.
 *
 * uk.co.saiman.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

@Component(service = CopleyService.class)
public class CopleyService {
  private final ServiceIndex<?, String, CopleyController> controllers;

  @Activate
  public CopleyService(BundleContext context) {
    controllers = ServiceIndex.open(context, CopleyController.class);
  }

  @Deactivate
  public void deactivate() {
    controllers.close();
  }

  public String getId(CopleyController controller) {
    return controllers
        .findRecord(controller)
        .stream()
        .flatMap(ServiceRecord::ids)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Cannot find id for controller " + controller));
  }

  public CopleyController getController(String id) {
    return controllers
        .highestRankedRecord(id)
        .tryGet()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find controller for id " + id))
        .serviceObject();
  }

  public Stream<String> getIds() {
    return controllers.ids();
  }

  public Stream<CopleyController> getControllers() {
    return controllers.records().map(ServiceRecord::serviceObject);
  }

  public Bundle getBundle(CopleyController controller) {
    return controllers
        .findRecord(controller)
        .orElseThrow(
            () -> new IllegalArgumentException("Cannot find bundle for controller " + controller))
        .bundle();
  }
}
