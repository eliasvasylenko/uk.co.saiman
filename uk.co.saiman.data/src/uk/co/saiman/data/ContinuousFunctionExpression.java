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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A basic wrapper around another continuous function which reflects all changes
 * in that function, and propagates events to listeners. The function which is
 * wrapped can be changed via {@link #setComponent(ContinuousFunction)}, which
 * will notify listeners of the modification.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public class ContinuousFunctionExpression<UD extends Quantity<UD>, UR extends Quantity<UR>>
    implements ContinuousFunctionDecorator<UD, UR> {
  private ContinuousFunction<UD, UR> component;
  private Disposable componentDisposable;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private HotObservable<ContinuousFunction<UD, UR>> changes = new HotObservable<>();

  /**
   * Create a default empty expression about the function
   * {@link ContinuousFunction#empty(Unit, Unit)}.
   * 
   * @param unitDomain
   *          the units of measurement of values in the domain
   * @param unitRange
   *          the units of measurement of values in the range
   */
  public ContinuousFunctionExpression(Unit<UD> unitDomain, Unit<UR> unitRange) {
    this(ContinuousFunction.empty(unitDomain, unitRange));
  }

  /**
   * Create an instance which is initially over the given component.
   * 
   * @param component
   *          The component to wrap
   */
  public ContinuousFunctionExpression(ContinuousFunction<UD, UR> component) {
    setComponent(component);
  }

  @Override
  public ContinuousFunction<UD, UR> getComponent() {
    try {
      lock.readLock().lock();
      return component;
    } finally {
      lock.readLock().unlock();
    }
  }

  /**
   * Change the component to be wrapped by this function.
   * 
   * @param component
   *          The continuous function we wish to wrap
   */
  public void setComponent(ContinuousFunction<UD, UR> component) {
    try {
      lock.writeLock().lock();
      if (this.componentDisposable != null)
        this.componentDisposable.cancel();
      this.component = component;
      this.componentDisposable = component.changes().observe(m -> changes.next(this));
    } finally {
      lock.writeLock().unlock();
    }

    changes.next(this);
  }

  @Override
  public ContinuousFunctionExpression<UD, UR> copy() {
    return new ContinuousFunctionExpression<>(getComponent().copy());
  }

  @Override
  public Observable<? extends ContinuousFunction<UD, UR>> changes() {
    return changes;
  }
}
