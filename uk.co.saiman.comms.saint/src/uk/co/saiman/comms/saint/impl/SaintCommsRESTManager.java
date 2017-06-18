/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.comms.saint.
 *
 * uk.co.saiman.comms.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.saint.impl;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.saint.SaintComms;

@Component
public class SaintCommsRESTManager {
  private final Map<SaintComms, CommsREST> restClasses = new HashMap<>();
  private final Map<CommsREST, ServiceRegistration<CommsREST>> serviceRegistrations = new HashMap<>();
  private BundleContext context;

  @Reference
  private DTOs dtos;

  @Activate
  synchronized void activate(BundleContext context) {
    this.context = context;
    restClasses.entrySet().stream().forEach(e -> register(e.getKey(), e.getValue()));
  }

  void register(SaintComms comms, CommsREST rest) {
    serviceRegistrations.put(rest, context.registerService(CommsREST.class, rest, null));
  }

  @Reference(policy = DYNAMIC, cardinality = MULTIPLE)
  synchronized void addComms(SaintComms comms) {
    try {
      CommsREST restService = new SaintCommsREST(comms, dtos);
      restClasses.put(comms, restService);

      if (context != null) {
        register(comms, restService);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  synchronized void removeComms(SaintComms comms) {
    ServiceRegistration<?> restService = serviceRegistrations.remove(restClasses.remove(comms));
    if (restService != null) {
      restService.unregister();
    }
  }
}
