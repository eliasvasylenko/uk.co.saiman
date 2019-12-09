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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.workspace;

import static java.util.function.Function.identity;

import java.util.stream.Stream;

import org.eclipse.e4.ui.model.application.MAddon;
import org.osgi.framework.BundleContext;

import uk.co.saiman.experiment.executor.Executor;
import uk.co.saiman.experiment.executor.service.ExecutorService;
import uk.co.saiman.osgi.ServiceIndex;
import uk.co.saiman.osgi.ServiceRecord;

public class EclipseExecutorService implements ExecutorService {
  public static final String EXECUTOR_SERVICE_ID = "uk.co.saiman.experiment.executors";

  private final ServiceIndex<ExecutorService, String, ExecutorService> executors;
  private final MAddon addon;

  public EclipseExecutorService(BundleContext bundleContext, MAddon addon) {
    this.executors = ServiceIndex.open(bundleContext, ExecutorService.class, identity());
    this.addon = addon;
  }

  public void close() {
    executors.close();
  }

  private java.util.Optional<ExecutorService> getBackingService() {
    return executors
        .highestRankedRecord(addon.getPersistedState().get(EXECUTOR_SERVICE_ID))
        .tryGet()
        .map(ServiceRecord::serviceObject);
  }

  @Override
  public Stream<Executor> executors() {
    return getBackingService().stream().flatMap(ExecutorService::executors);
  }

  @Override
  public Executor getExecutor(String id) {
    return getBackingService().orElseThrow().getExecutor(id);
  }

  @Override
  public String getId(Executor procedure) {
    return getBackingService().orElseThrow().getId(procedure);
  }
}
