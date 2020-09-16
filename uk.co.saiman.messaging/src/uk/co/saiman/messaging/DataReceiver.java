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
 * This file is part of uk.co.saiman.messaging.
 *
 * uk.co.saiman.messaging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.messaging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import uk.co.saiman.observable.Observable;

/**
 * A receiver for a stream of data. Bytes are not necessarily received in the
 * same unit-blocks in which they were sent, but they are guaranteed to be in
 * order.
 * 
 * @author Elias N Vasylenko
 */
public interface DataReceiver extends DataEndpoint {
  Observable<ByteBuffer> receiveData();

  default DataBuffer openDataBuffer(int size) throws IOException {
    return new DataReceiverBuffer(this, size);
  }

  default DataReceiver packeting(int size) {
    return new PacketingDataReceiver(this, size);
  }

  default ReadableByteChannel openByteChannel(int bufferSize) throws IOException {
    return DataByteChannel.over(this, bufferSize);
  }
}
