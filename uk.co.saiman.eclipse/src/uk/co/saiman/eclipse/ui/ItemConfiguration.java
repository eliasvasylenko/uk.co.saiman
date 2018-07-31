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
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui;

import static java.util.function.Function.identity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.input.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;

public interface ItemConfiguration<T> {
  ItemConfiguration<T> setObject(T object);

  ItemConfiguration<T> setUpdateFunction(Consumer<? super T> update);

  ItemConfiguration<T> setDataFormat(
      DataFormat format,
      Function<T, String> encode,
      Function<String, T> decode);

  default <U> ItemConfiguration<T> setDataFormat(
      TextFormat<U> format,
      Function<T, U> encode,
      Function<U, T> decode) {
    return setDataFormat(
        DataFormat.PLAIN_TEXT, // TODO lookupMimeType(format.getMimeType())
        data -> format.encodeString(new Payload<>(encode.apply(data))),
        string -> decode.apply(format.decodeString(string).data));
  }

  default ItemConfiguration<T> setDataFormat(TextFormat<T> format) {
    return setDataFormat(format, identity(), identity());
  }

  /**
   * Set the drag handler for the given transfer modes.
   * 
   * @param checkDrag
   * @param handleDrag
   * @param transferModes
   */
  ItemConfiguration<T> setDragHandlers(
      Predicate<? super TransferOut<? extends T>> checkDrag,
      Consumer<? super TransferOut<? extends T>> handleDrag,
      TransferMode... transferModes);

  /**
   * Set the drag handler for the given transfer modes.
   * 
   * @param handleDrag
   * @param transferModes
   */
  default ItemConfiguration<T> setDragHandlers(
      Consumer<? super TransferOut<? extends T>> handleDrag,
      TransferMode... transferModes) {
    return setDragHandlers(c -> true, handleDrag, transferModes);
  }

  /**
   * Set the drop handler for the given transfer modes.
   * 
   * @param checkDrop
   * @param handleDrop
   * @param transferModes
   */
  ItemConfiguration<T> setDropHandlers(
      Predicate<? super TransferIn<? extends T>> checkDrop,
      Consumer<? super TransferIn<? extends T>> handleDrop,
      TransferMode... transferModes);

  /**
   * Set the drop handler for the given transfer modes.
   * 
   * @param handleDrop
   * @param transferModes
   */
  default ItemConfiguration<T> setDropHandlers(
      Consumer<? super TransferIn<? extends T>> handleDrop,
      TransferMode... transferModes) {
    return setDropHandlers(c -> true, handleDrop, transferModes);
  }
}
