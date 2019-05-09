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

import uk.co.saiman.experiment.instruction.Executor;
import uk.co.saiman.experiment.instruction.ExecutorService;
import uk.co.saiman.osgi.ServiceIndex;

@Component
public class ExecutorServiceImpl implements ExecutorService {
  private final ServiceIndex<?, String, Executor<?>> executors;

  @Activate
  public ExecutorServiceImpl(BundleContext context) {
    executors = ServiceIndex.open(context, Executor.class.getName());
  }

  @Override
  public Stream<Executor<?>> executors() {
    return executors.objects();
  }

  @Override
  public Executor<?> getExecutor(String id) {
    return executors.get(id).get().serviceObject();
  }

  @Override
  public String getId(Executor<?> executor) {
    return executors.findRecord(executor).get().id();
  }
}
