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

import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.input.DataFormat;
import uk.co.saiman.data.format.TextFormat;

public interface ListItemConfiguration<T> extends ItemConfiguration<T> {
  @Override
  default ListItemConfiguration<T> setObject(T object) {
    return setObjects(asList(object));
  }

  @Override
  ListItemConfiguration<T> setUpdateFunction(Consumer<? super T> update);

  ListItemConfiguration<T> setObjects(List<? extends T> objects);

  ListItemConfiguration<T> setUpdateFunction(BiConsumer<? super Integer, ? super T> update);

  @Override
  ListItemConfiguration<T> setDataFormat(
      DataFormat format,
      Function<T, String> encode,
      Function<String, T> decode);

  @Override
  default <U> ListItemConfiguration<T> setDataFormat(
      TextFormat<U> format,
      Function<T, U> encode,
      Function<U, T> decode) {
    ItemConfiguration.super.setDataFormat(format, encode, decode);
    return this;
  }

  @Override
  default ListItemConfiguration<T> setDataFormat(TextFormat<T> format) {
    ItemConfiguration.super.setDataFormat(format);
    return this;
  }

  @Override
  ListItemConfiguration<T> setDragHandlers(
      Predicate<? super TransferOut<? extends T>> checkDrag,
      Consumer<? super TransferOut<? extends T>> handleDrag,
      TransferMode... transferModes);

  @Override
  default ListItemConfiguration<T> setDragHandlers(
      Consumer<? super TransferOut<? extends T>> handleDrag,
      TransferMode... transferModes) {
    ItemConfiguration.super.setDragHandlers(handleDrag, transferModes);
    return this;
  }

  @Override
  ListItemConfiguration<T> setDropHandlers(
      Predicate<? super TransferIn<? extends T>> checkDrop,
      Consumer<? super TransferIn<? extends T>> handleDrop,
      TransferMode... transferModes);

  @Override
  default ListItemConfiguration<T> setDropHandlers(
      Consumer<? super TransferIn<? extends T>> handleDrop,
      TransferMode... transferModes) {
    ItemConfiguration.super.setDropHandlers(handleDrop, transferModes);
    return this;
  }
}
