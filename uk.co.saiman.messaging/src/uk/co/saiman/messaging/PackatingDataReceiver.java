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

import java.nio.ByteBuffer;

import uk.co.saiman.observable.Disposable;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.observable.Observable;

class PackatingDataReceiver implements DataReceiver {
  private ByteBuffer packet;
  private final HotObservable<ByteBuffer> downstream;

  public PackatingDataReceiver(DataReceiver receiver, int size) {
    packet = ByteBuffer.allocate(size);
    downstream = new HotObservable<>() {
      private Disposable observation;

      protected void open() throws Exception {
        observation = receiver
            .receiveData()
            .weakReference(PackatingDataReceiver.this)
            .observe(o -> o.owner().send(o.message()));
      }

      protected void close() throws Exception {
        observation.cancel();
      }
    };
  }

  public synchronized void send(ByteBuffer buffer) {
    while (buffer.remaining() >= packet.remaining()) {
      byte[] bytes = new byte[packet.remaining()];
      buffer.get(bytes);
      packet.put(bytes);
      sendDownstream();
    }
    packet.put(buffer);
  }

  private synchronized void sendDownstream() {
    packet.flip();
    downstream.next(packet);
    packet = ByteBuffer.allocate(packet.capacity());
  }

  @Override
  public Observable<ByteBuffer> receiveData() {
    return downstream;
  }
}
