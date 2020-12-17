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
package uk.co.saiman.data.function.format;

import static java.nio.ByteBuffer.allocate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.RegularSampledDomain;
import uk.co.saiman.data.function.SampledContinuousFunction;

/**
 * @author Elias N Vasylenko
 *
 * @param <UD> the type of the units of measurement of values in the domain
 * @param <UR> the type of the units of measurement of values in the range
 */
public class RegularSampledContinuousFunctionFormat<UD extends Quantity<UD>, UR extends Quantity<UR>>
    implements DataFormat<SampledContinuousFunction<UD, UR>> {
  private static final String MASS_SPECTRUM_RANGE_EXTENSION = "msr";
  private static final String MASS_SPECTRUM_FUNCTION_EXTENSION = "msf";

  private final RegularSampledDomain<UD> domain;
  private final Unit<UR> rangeUnit;

  protected RegularSampledContinuousFunctionFormat(RegularSampledDomain<UD> domain, Unit<UR> rangeUnit) {
    this.domain = domain;
    this.rangeUnit = rangeUnit;
  }

  @Override
  public Stream<String> getExtensions() {
    return Stream.of(MASS_SPECTRUM_RANGE_EXTENSION);
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param domain    a description of the domain of the function to format
   * @param rangeUnit the type of the units of measurement of values in the range
   * @return a continuous function byte format for the given domain frequency and
   *         domain start
   */
  public static <UD extends Quantity<UD>, UR extends Quantity<UR>> DataFormat<SampledContinuousFunction<UD, UR>> overDomain(
      RegularSampledDomain<UD> domain,
      Unit<UR> rangeUnit) {
    return new RegularSampledContinuousFunctionFormat<>(domain, rangeUnit);
  }

  /**
   * @param domainUnit the type of the units of measurement of values in the
   *                   domain
   * @param rangeUnit  the type of the units of measurement of values in the range
   * @return a continuous function byte format which encodes the domain start and
   *         domain frequency
   */
  public static <UD extends Quantity<UD>, UR extends Quantity<UR>> DataFormat<SampledContinuousFunction<UD, UR>> overEncodedDomain(
      Unit<UD> domainUnit,
      Unit<UR> rangeUnit) {
    return new DataFormat<SampledContinuousFunction<UD, UR>>() {
      @Override
      public Stream<String> getExtensions() {
        return Stream.of(MASS_SPECTRUM_FUNCTION_EXTENSION);
      }

      @Override
      public Stream<MediaType> getMediaTypes() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Payload<SampledContinuousFunction<UD, UR>> load(ReadableByteChannel inputChannel) throws IOException {
        RegularSampledDomain<UD> domain = new RegularSampledDomainFormat<>(domainUnit).load(inputChannel).data;
        return new Payload<>(overDomain(domain, rangeUnit).load(inputChannel).data);
      }

      @Override
      public void save(WritableByteChannel outputChannel, Payload<? extends SampledContinuousFunction<UD, UR>> payload)
          throws IOException {
        RegularSampledDomain<UD> domain = (RegularSampledDomain<UD>) payload.data.domain();
        new RegularSampledDomainFormat<>(domainUnit).save(outputChannel, new Payload<>(domain));
        overDomain(domain, rangeUnit).save(outputChannel, payload);
      }
    };
  }

  @Override
  public Payload<SampledContinuousFunction<UD, UR>> load(ReadableByteChannel inputChannel) throws IOException {
    ByteBuffer buffer = allocate(Double.BYTES * domain.getDepth());
    while (buffer.hasRemaining()) {
      inputChannel.read(buffer);
    }
    buffer.flip();

    double[] intensities = new double[domain.getDepth()];
    buffer.asDoubleBuffer().get(intensities);

    return new Payload<>(new ArraySampledContinuousFunction<>(domain, rangeUnit, intensities));
  }

  @Override
  public void save(WritableByteChannel outputChannel, Payload<? extends SampledContinuousFunction<UD, UR>> payload)
      throws IOException {
    if (!(payload.data.domain() instanceof RegularSampledDomain<?>)) {
      throw new IllegalArgumentException();
    }
    RegularSampledDomain<UD> dataDomain = (RegularSampledDomain<UD>) payload.data.domain();

    if (dataDomain.getFrequency() != domain.getFrequency()) {
      throw new IllegalArgumentException(dataDomain.getFrequency() + " != " + domain.getFrequency());
    }
    if (!Objects.equals(dataDomain.getInterval().getLeftEndpoint(), domain.getInterval().getLeftEndpoint())) {
      throw new IllegalArgumentException(
          dataDomain.getInterval().getLeftEndpoint() + " != " + domain.getInterval().getLeftEndpoint());
    }
    if (dataDomain.getDepth() != domain.getDepth()) {
      throw new IllegalArgumentException(dataDomain.getDepth() + " != " + domain.getDepth());
    }

    ByteBuffer buffer = allocate(Double.BYTES * payload.data.getDepth());
    for (int i = 0; i > payload.data.getDepth(); i++) {
      buffer.putDouble(payload.data.range().getSample(i));
    }
    buffer.flip();
    while (buffer.hasRemaining()) {
      outputChannel.write(buffer);
    }
  }
}
