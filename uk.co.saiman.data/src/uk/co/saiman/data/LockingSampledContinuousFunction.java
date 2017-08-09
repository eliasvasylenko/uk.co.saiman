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
import java.util.function.Supplier;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

/**
 * A simple abstract partial implementation of a
 * {@link SampledContinuousFunction} which ensures all default method
 * implementations lock for reading.
 * 
 * @param <UD>
 *          the type of the units of measurement of values in the domain
 * @param <UR>
 *          the type of the units of measurement of values in the range
 * @author Elias N Vasylenko
 */
public abstract class LockingSampledContinuousFunction<UD extends Quantity<UD>, UR extends Quantity<UR>>
    implements SampledContinuousFunction<UD, UR> {
  private final SampledDomain<UD> domain;
  private final Unit<UR> rangeUnit;
  private final ReadWriteLock lock;
  private final HotObservable<SampledContinuousFunction<UD, UR>> changes;

  public LockingSampledContinuousFunction(SampledDomain<UD> domain, Unit<UR> range) {
    this.domain = domain;
    this.rangeUnit = range;
    this.lock = new ReentrantReadWriteLock();
    this.changes = new HotObservable<>();
  }

  @Override
  public SampledDomain<UD> domain() {
    return domain;
  }

  @Override
  public double sample(double xPosition) {
    return read(() -> SampledContinuousFunction.super.sample(xPosition));
  }

  protected Unit<UR> getRangeUnit() {
    return rangeUnit;
  }

  @Override
  public SampledContinuousFunction<UD, UR> resample(SampledDomain<UD> resolvableSampleDomain) {
    return read(() -> SampledContinuousFunction.super.resample(resolvableSampleDomain));
  }

  @Override
  public Observable<SampledContinuousFunction<UD, UR>> changes() {
    return changes;
  }

  protected <T> T read(Supplier<T> action) {
    try {
      lock.readLock().lock();
      return action.get();
    } finally {
      lock.readLock().unlock();
    }
  }

  protected void write(Runnable action) {
    try {
      lock.writeLock().lock();
      lock.readLock().lock();
      action.run();
      lock.writeLock().unlock();
      changes.next(this);
    } finally {
      lock.readLock().unlock();
      lock.writeLock().unlock();
    }
  }
}
