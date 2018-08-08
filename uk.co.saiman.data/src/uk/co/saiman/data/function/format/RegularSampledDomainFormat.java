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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.stream.Stream;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.function.RegularSampledDomain;

public class RegularSampledDomainFormat<U extends Quantity<U>>
    implements DataFormat<RegularSampledDomain<U>> {
  private static final int SIZE = Double.BYTES * 2 + Integer.BYTES;
  private static final String MASS_SPECTRUM_DOMAIN_EXTENSION = "msd";

  private final Unit<U> domainUnit;

  public RegularSampledDomainFormat(Unit<U> domainUnit) {
    this.domainUnit = domainUnit;
  }

  @Override
  public String getExtension() {
    return MASS_SPECTRUM_DOMAIN_EXTENSION;
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Payload<RegularSampledDomain<U>> load(ReadableByteChannel inputChannel)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(SIZE);
    do {
      inputChannel.read(buffer);
    } while (buffer.hasRemaining());
    buffer.flip();
    double frequency = buffer.getDouble();
    double domainStart = buffer.getDouble();
    int depth = buffer.getInt();

    return new Payload<>(new RegularSampledDomain<>(domainUnit, depth, frequency, domainStart));
  }

  @Override
  public void save(
      WritableByteChannel outputChannel,
      Payload<? extends RegularSampledDomain<U>> domain) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(SIZE);
    buffer.putDouble(domain.data.getFrequency());
    buffer.putDouble(domain.data.getInterval().getLeftEndpoint());
    buffer.putInt(domain.data.getDepth());
    buffer.flip();
    do {
      outputChannel.write(buffer);
    } while (buffer.hasRemaining());
  }
}
