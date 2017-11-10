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
package uk.co.saiman.data.format;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

/**
 * A binary data format for loading and saving to and from a certain object
 * type.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of data
 */
public interface DataFormat<T> {
  String getId();

  /**
   * @return the default path extension for files of this format
   */
  String getExtension();

  default TypeToken<T> getType() {
    return forType(getClass())
        .resolveSupertype(DataFormat.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }

  /**
   * Load an object from a readable byte channel.
   * 
   * <p>
   * The calling context should take care of opening and cleaning up resources.
   * The channel is assumed to be already open upon invocation, and should not
   * be closed by any implementing method.
   * 
   * @param inputChannel
   *          the input byte source
   * @return the object represented by the bytes
   * @throws IOException
   *           it's an IO operation after all...
   */
  Payload<? extends T> load(ReadableByteChannel inputChannel) throws IOException;

  /**
   * Save an object to a writable byte channel.
   * 
   * <p>
   * The calling context should take care of opening and cleaning up resources.
   * The channel is assumed to be already open upon invocation, and should not
   * be closed by any implementing method.
   * 
   * @param outputChannel
   *          the output byte sink
   * @param payload
   *          the object represented by the bytes
   * @throws IOException
   *           it's an IO operation after all...
   */
  void save(WritableByteChannel outputChannel, Payload<? extends T> payload) throws IOException;
}
