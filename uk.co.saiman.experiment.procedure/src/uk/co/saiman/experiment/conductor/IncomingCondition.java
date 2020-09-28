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
 * This file is part of uk.co.saiman.experiment.conductor.
 *
 * uk.co.saiman.experiment.conductor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.conductor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.conductor;

import static java.lang.String.format;

import uk.co.saiman.experiment.conductor.IncomingDependencies.IncomingDependencyState;
import uk.co.saiman.experiment.declaration.ExperimentPath.Absolute;
import uk.co.saiman.experiment.dependency.Condition;
import uk.co.saiman.experiment.dependency.ConditionClosedException;
import uk.co.saiman.experiment.dependency.ConditionPath;

class IncomingCondition<T> {
  private final OutgoingCondition<T> outgoing;
  private final java.util.concurrent.locks.Condition lockCondition;
  private IncomingDependencyState state;

  public IncomingCondition(
      OutgoingCondition<T> outgoing,
      java.util.concurrent.locks.Condition lockCondition) {
    this.outgoing = outgoing;
    this.lockCondition = lockCondition;
    this.state = IncomingDependencyState.WAITING;
  }

  public Condition<T> acquire() {
    try {
      while (!outgoing.beginAcquire(this)) {
        if (state == IncomingDependencyState.DONE) {
          throw new ConductorException(
              format(
                  "Failed to prepare dependency to condition %s at %s",
                  outgoing.type(),
                  outgoing.path()));
        }
        lockCondition.await();
      }
      state = IncomingDependencyState.ACQUIRED;
    } catch (InterruptedException e) {
      throw new ConductorException(
          format(
              "Failed to acquire dependency to condition %s at %s",
              outgoing.type(),
              outgoing.path()),
          e);
    }
    return new Condition<T>() {
      @SuppressWarnings("unchecked")
      @Override
      public Class<T> type() {
        return (Class<T>) path().getProduction();
      }

      @Override
      public ConditionStatus status() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public ConditionPath<Absolute, T> path() {
        return path();
      }

      @Override
      public T value() {
        outgoing.lock().lock();
        try {
          if (getState() == IncomingDependencyState.DONE) {
            throw new ConditionClosedException(type());
          }
          return outgoing.nextResource();
        } finally {
          outgoing.lock().unlock();
        }
      }

      @Override
      public void close() {
        outgoing.lock().lock();
        try {
          done();
        } finally {
          outgoing.lock().unlock();
        }
      }
    };
  }

  void invalidateIncoming() {
    state = IncomingDependencyState.WAITING;
    outgoing.invalidatedIncoming(this);
  }

  void invalidatedOutgoing() {
    state = IncomingDependencyState.WAITING;
  }

  public void done() {
    if (this.state != IncomingDependencyState.DONE) {
      this.state = IncomingDependencyState.DONE;
      this.lockCondition.signalAll();
    }
  }

  public IncomingDependencyState getState() {
    return this.state;
  }

  public Class<T> type() {
    return outgoing.type();
  }
}