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
 * This file is part of uk.co.saiman.instrument.acquisition.
 *
 * uk.co.saiman.instrument.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.acquisition;

import static java.util.Objects.requireNonNull;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import uk.co.saiman.data.function.ContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.data.function.SampledDomain;
import uk.co.saiman.data.function.SampledRange;

/**
 * A continuous function factory backed internally by a shared pool of arrays to
 * avoid unnecessary memory allocation.
 * <p>
 * Previously allocated arrays are stored by soft reference such that they can
 * be GCd if and when necessary to free heap space, but otherwise will remain
 * available for immediate reuse as soon as the previous continuous function
 * they were used for becomes unreachable.
 * <p>
 * Due to the large default cache depth, this can appear to use a huge amount of
 * memory, but this is not really the case. Firstly the amount allocated will
 * never exceed the peak amount allocated without using the buffer, and secondly
 * almost all the allocated memory at any given time should be unreachable and
 * therefore available for collection if there is enough pressure on the GC.
 * <p>
 * TODO Different deployments are likely to have different GC behavior so it may
 * be wise at some point to provide a service with configurable cache depth and
 * trace logging.
 * 
 * @author Elias N Vasylenko
 */
public class AcquisitionBufferPool {
  private static final int DEFAULT_CACHE_DEPTH = 512;

  class IntensitiesReference {
    private final SoftReference<double[]> reference;
    private final double[] data;

    public IntensitiesReference(double[] data, SoftReference<double[]> reference) {
      this.data = data;
      this.reference = reference;
    }

    public IntensitiesReference(double[] data) {
      this(data, new SoftReference<>(data));
    }
  }

  class ClearingReference extends WeakReference<ContinuousFunction<?, ?>> {
    private boolean cleared;

    public ClearingReference(ContinuousFunction<?, ?> referent) {
      super(referent, queue);
    }
  }

  private final SampledDomain<Time> domain;
  private final Unit<Dimensionless> intensityUnits;

  private final int sparesToKeep;
  private final ReferenceQueue<ContinuousFunction<?, ?>> queue = new ReferenceQueue<>();
  private final Map<SoftReference<double[]>, ClearingReference> availableBuffers;

  public AcquisitionBufferPool(SampledDomain<Time> domain, Unit<Dimensionless> intensityUnits) {
    this(domain, intensityUnits, DEFAULT_CACHE_DEPTH);
  }

  public AcquisitionBufferPool(
      SampledDomain<Time> domain,
      Unit<Dimensionless> intensityUnits,
      int sparesToKeep) {
    this.domain = requireNonNull(domain);
    this.intensityUnits = requireNonNull(intensityUnits);
    this.sparesToKeep = sparesToKeep;
    this.availableBuffers = new HashMap<>();
  }

  public SampledContinuousFunction<Time, Dimensionless> fillNextBuffer(
      Consumer<double[]> fillBuffer) {
    IntensitiesReference intensities = getAvailableBuffer();

    fillBuffer.accept(intensities.data);

    SampledContinuousFunction<Time, Dimensionless> function = createContinuousFunction(
        domain,
        intensityUnits,
        intensities.data);

    availableBuffers.put(intensities.reference, new ClearingReference(function));

    return function;
  }

  private static SampledContinuousFunction<Time, Dimensionless> createContinuousFunction(
      SampledDomain<Time> domain,
      Unit<Dimensionless> intensityUnits,
      double[] intensities) {
    return new SampledContinuousFunction<Time, Dimensionless>() {
      @Override
      public SampledDomain<Time> domain() {
        return domain;
      }

      @Override
      public SampledRange<Dimensionless> range() {
        return new SampledRange<Dimensionless>(this) {
          @Override
          public double getSample(int index) {
            return intensities[index];
          }

          @Override
          public int getDepth() {
            return domain.getDepth();
          }

          @Override
          public Unit<Dimensionless> getUnit() {
            return intensityUnits;
          }
        };
      }

      @Override
      public int getDepth() {
        return domain.getDepth();
      }
    };
  }

  private IntensitiesReference getAvailableBuffer() {
    Reference<? extends ContinuousFunction<?, ?>> reference;
    while ((reference = queue.poll()) != null) {
      ((ClearingReference) reference).cleared = true;
    }

    int sparesToKeep = this.sparesToKeep;
    double[] intensities = null;
    SoftReference<double[]> intensitiesReference = null;

    for (SoftReference<double[]> buffer : new ArrayList<>(availableBuffers.keySet())) {
      if (availableBuffers.get(buffer).cleared) {
        if (intensities == null) {
          intensitiesReference = buffer;
          intensities = buffer.get();
        }
        if (buffer.get() == null || sparesToKeep-- <= 0) {
          availableBuffers.remove(buffer);
        }
      }
    }

    if (intensities != null) {
      return new IntensitiesReference(intensities, intensitiesReference);
    }

    return new IntensitiesReference(new double[domain.getDepth()]);
  }
}
