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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Set;
import java.util.stream.Stream;

import javafx.scene.input.ClipboardContent;
import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.fx.MediaTypes;
import uk.co.saiman.eclipse.ui.fx.TransferCellOut;

public class TransferCellOutImpl<T> implements TransferCellOut {
  private final Item<T> item;

  private final Set<TransferMode> transferModes;
  private final ClipboardContent clipboardContent;

  public TransferCellOutImpl(Item<T> item) {
    this.item = item;

    // determine available transfer modes
    this.transferModes = unmodifiableSet(
        Stream
            .of(TransferMode.values())
            .filter(mode -> item.getRemove().isPresent() || !mode.isDestructive())
            .collect(toSet()));

    // populate clipboard
    this.clipboardContent = new ClipboardContent();

    item.getTransferFormats().forEach(transferFormat -> {
      DataFormat<T> dataFormat = (DataFormat<T>) transferFormat.dataFormat();
      Object data;
      Payload<T> payload = new Payload<>(item.getObject());
      if (dataFormat instanceof TextFormat<?>) {
        data = ((TextFormat<T>) dataFormat).encodeString(payload);

      } else {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
          dataFormat.save(Channels.newChannel(bytes), payload);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        data = bytes.toByteArray();
      }
      dataFormat
          .getMediaTypes()
          .map(MediaTypes::toDataFormat)
          .distinct()
          .forEach(format -> clipboardContent.put(format, data));
    });
  }

  @Override
  public Set<TransferMode> supportedTransferModes() {
    return transferModes;
  }

  @Override
  public void handle(TransferMode transferMode) {
    if (!transferModes.contains(transferMode)) {
      throw new IllegalArgumentException("Unsupported transfer mode " + transferMode);
    }
    if (transferMode.isDestructive()) {
      item.getRemove().get().run();
    }
  }

  @Override
  public ClipboardContent getClipboardContent() {
    return clipboardContent;
  }
}
