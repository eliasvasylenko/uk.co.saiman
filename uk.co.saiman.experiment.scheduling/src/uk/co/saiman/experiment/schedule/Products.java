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
 * This file is part of uk.co.saiman.experiment.scheduling.
 *
 * uk.co.saiman.experiment.scheduling is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.scheduling is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.schedule;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.path.ProductPath;
import uk.co.saiman.experiment.procedure.Procedure;
import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Result;
import uk.co.saiman.observable.Observable;

public class Products {
  private final Scheduler scheduler;
  private final Schedule schedule;

  public Products(Scheduler scheduler) {
    this.scheduler = scheduler;
    this.schedule = scheduler.getSchedule().get();
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public Procedure getProcedure() {
    return schedule.getProcedure();
  }

  public Stream<Product> products() {
    throw new UnsupportedOperationException();
  }

  public Optional<Result<?>> resolveResult(ProductPath<?> result) {
    throw new UnsupportedOperationException();
  }

  public <T extends Result<?>> T resolveResult(Dependency<T, ?> path) {
    throw new UnsupportedOperationException();
  }

  public <T extends Product> Observable<T> products(Dependency<T, ?> path) {
    throw new UnsupportedOperationException();
  }

  public synchronized void interrupt() {
    scheduler.interrupt(this);
  }

  public synchronized void clear() throws IOException {
    scheduler.clear(this);
  }
}
