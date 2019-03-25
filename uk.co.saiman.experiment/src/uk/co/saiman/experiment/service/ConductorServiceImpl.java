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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.service;

import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.procedure.Conductor;
import uk.co.saiman.experiment.procedure.ConductorService;
import uk.co.saiman.osgi.ServiceIndex;

@Component
public class ConductorServiceImpl implements ConductorService {
  private final ServiceIndex<?, String, Conductor<?>> procedures;

  @Activate
  public ConductorServiceImpl(BundleContext context) {
    procedures = ServiceIndex.open(context, Conductor.class.getName());
  }

  @Override
  public Stream<Conductor<?>> conductors() {
    return procedures.objects();
  }

  @Override
  public Conductor<?> getConductor(String id) {
    return procedures.get(id).get().serviceObject();
  }

  @Override
  public String getId(Conductor<?> procedure) {
    return procedures.findRecord(procedure).get().id();
  }
}
