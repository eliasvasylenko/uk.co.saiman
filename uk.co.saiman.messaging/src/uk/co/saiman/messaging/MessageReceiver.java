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

import uk.co.saiman.observable.Observable;

/**
 * A receiver for a sequence of self-contained messages. Bytes are received in
 * the same unit-blocks in which they were sent, but they may not be guaranteed
 * to be in order.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface MessageReceiver extends DataReceiver {
  @Override
  default Observable<ByteBuffer> receiveData() {
    return receiveMessages();
  }

  default MessageBuffer openMessageBuffer(int size) throws IOException {
    return new MessageReceiverBuffer(this, size);
  }

  /**
   * Receive a single cohesive message.
   */
  Observable<ByteBuffer> receiveMessages();
}
