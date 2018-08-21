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
package uk.co.saiman.data.format;

import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newOutputStream;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A binary data format for loading and saving to and from a certain object
 * type.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of data
 */
public interface TextFormat<T> extends DataFormat<T> {
  @Override
  default Payload<? extends T> load(ReadableByteChannel inputChannel) throws IOException {
    return decodeString(loadString(inputChannel));
  }

  default String loadString(ReadableByteChannel inputChannel) {
    return new BufferedReader(new InputStreamReader(newInputStream(inputChannel)))
        .lines()
        .collect(joining());
  }

  Payload<? extends T> decodeString(String string);

  @Override
  default void save(WritableByteChannel outputChannel, Payload<? extends T> payload)
      throws IOException {
    saveString(outputChannel, encodeString(payload));
  }

  default void saveString(WritableByteChannel outputChannel, String string) throws IOException {
    try (PrintWriter out = new PrintWriter(newOutputStream(outputChannel))) {
      out.println(string);
    }
  }

  String encodeString(Payload<? extends T> payload);
}
