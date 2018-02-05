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

import static org.osgi.service.component.annotations.ReferenceCardinality.MANDATORY;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
  private final Set<SaintComms> comms = new HashSet<>();
  private final Map<SaintComms, ServiceRegistration<CommsREST>> serviceRegistrations = new HashMap<>();
  private BundleContext context;

  @Reference(cardinality = MANDATORY)
  private DTOs dtos;

  @Activate
  synchronized void activate(BundleContext context) {
    this.context = context;
    comms.stream().forEach(e -> register(e));
  }

  void register(SaintComms comms) {
    CommsREST rest = new SaintCommsREST(comms, dtos);
    serviceRegistrations.put(comms, context.registerService(CommsREST.class, rest, null));
  }

  @Reference(policy = DYNAMIC, cardinality = MULTIPLE)
  synchronized void addComms(SaintComms comms) {
    try {
      this.comms.add(comms);

      if (context != null) {
        register(comms);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  synchronized void removeComms(SaintComms comms) {
    this.comms.remove(comms);
    ServiceRegistration<?> restService = serviceRegistrations.remove(comms);
    if (restService != null) {
      restService.unregister();
    }
  }
}
