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
package uk.co.saiman.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * The concept of an experiment in a {@link Workspace workspace} is represented
 * by a hierarchy of nodes. The workspace provides an interface for managing
 * those experiments.
 * <p>
 * A workspace contains a register of {@link ExperimentProcedure experiment
 * types}. Experiment nodes can be created according to these types.
 * 
 * @author Elias N Vasylenko
 */
public class Workspace {
  private final Set<Experiment> experiments = new HashSet<>();

  private final HotObservable<ExperimentEvent> events = new HotObservable<>();

  void fireEvent(ExperimentEvent event) {
    events.next(event);
  }

  public Observable<ExperimentEvent> events() {
    return events;
  }

  /**
   * Get all experiments of the {@link #getExperiments() root experiment type}.
   * 
   * @return all registered root experiment parts
   */
  public Stream<Experiment> getExperiments() {
    synchronized (experiments) {
      return new ArrayList<>(experiments).stream();
    }
  }

  public Optional<Experiment> getExperiment(String id) {
    synchronized (experiments) {
      return getExperiments().filter(c -> c.getId().equals(id)).findAny();
    }
  }

  /**
   * Add a root experiment node to management.
   * 
   * @param experiment
   *          the experiment to add
   */
  public synchronized void addExperiment(Experiment experiment) {
    synchronized (experiments) {
      if (experiments.contains(experiment)) {
        return;
      }
    }
    experiment.lockExperiment().run(() -> experiment.addExperimentTo(this));
  }

  void addExperimentImpl(Experiment experiment) {
    synchronized (experiments) {
      experiments.add(experiment);
    }
  }

  public synchronized void removeExperiment(Experiment experiment) {
    synchronized (experiments) {
      if (!experiments.contains(experiment)) {
        return;
      }
    }
    experiment.lockExperiment().run(() -> experiment.removeExperimentFrom(this));
  }

  void removeExperimentImpl(Experiment experiment) {
    synchronized (experiments) {
      experiments.remove(experiment);
    }
  }
}
