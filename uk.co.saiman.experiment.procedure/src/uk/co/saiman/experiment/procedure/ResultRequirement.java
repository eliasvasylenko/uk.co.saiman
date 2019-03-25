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
package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Observation;
import uk.co.saiman.experiment.product.Production;
import uk.co.saiman.experiment.product.Result;

/**
 * An input to an experiment procedure should be wired up to an observation made
 * by a preceding procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the result we wish to find
 */
public class ResultRequirement<T> extends ProductRequirement<Result<T>> {
  public enum Cardinality {
    SINGULAR, PLURAL
  }

  private final Class<T> type;
  private final Cardinality cardinality;

  public ResultRequirement(String id, Class<T> type, Cardinality cardinality) {
    super(id);
    this.type = type;
    this.cardinality = cardinality;
  }

  public ResultRequirement(String id, Class<T> type) {
    this(id, type, Cardinality.SINGULAR);
  }

  public ResultRequirement(Observation<T> observation) {
    this(observation.id(), observation.type());
  }

  public Class<T> type() {
    return type;
  }

  public Cardinality cardinality() {
    return cardinality;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Observation<T>> resolveDependency(Production<?> capability) {
    return capability instanceof Observation<?>
        && type().isAssignableFrom(((Observation<?>) capability).type())
            ? Optional.of((Observation<T>) capability)
            : Optional.empty();
  }

  @Override
  public Stream<Observation<T>> resolveDependencies(Conductor<?> procedure) {
    return Productions
        .observations(procedure)
        .map(this::resolveDependency)
        .flatMap(Optional::stream);
  }
}
