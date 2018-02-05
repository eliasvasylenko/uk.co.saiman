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
package uk.co.saiman.eclipse.treeview;

import static java.util.function.Function.identity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.input.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.format.TextFormat;

/**
 * An interface for defining clipboard transfer behaviour for e.g.
 * cut/copy/paste or drag-and-drop.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 */
public interface TreeClipboard<T> {
  void addDataFormat(DataFormat format, Function<T, String> encode, Function<String, T> decode);

  default <U> void addDataFormat(
      TextFormat<U> format,
      Function<T, U> encode,
      Function<U, T> decode) {
    addDataFormat(
        DataFormat.PLAIN_TEXT, // TODO lookupMimeType(format.getMimeType())
        data -> format.encodeString(new Payload<>(encode.apply(data))),
        string -> decode.apply(format.decodeString(string).data));
  }

  default void addDataFormat(TextFormat<T> format) {
    addDataFormat(format, identity(), identity());
  }

  /**
   * If multiple drag handlers are added, higher priority is given to those added
   * later. If a drag handler is not applicable to a transfer, handling falls back
   * to the handler with the next highest priority.
   * 
   * @param checkDrag
   * @param handleDrag
   * @param transferMode
   */
  void addDragHandler(
      Predicate<? super TreeDragCandidate<? extends T>> checkDrag,
      Consumer<? super TreeDragCandidate<? extends T>> handleDrag,
      TreeTransferMode... transferMode);

  default void addDragHandler(
      Consumer<? super TreeDragCandidate<? extends T>> handleDrag,
      TreeTransferMode... transferMode) {
    addDragHandler(c -> true, handleDrag, transferMode);
  }

  void addDropHandler(
      Predicate<? super TreeDropCandidate<? extends T>> checkDrop,
      Consumer<? super TreeDropCandidate<? extends T>> handleDrop,
      TreeTransferMode... transferMode);

  default void addDropHandler(
      Consumer<? super TreeDropCandidate<? extends T>> handleDrop,
      TreeTransferMode... transferMode) {
    addDropHandler(c -> true, handleDrop, transferMode);
  }
}
