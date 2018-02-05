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

import static uk.co.saiman.reflection.token.TypeToken.forType;
import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

public interface TreeChildren {
  void addChild(int index, TypedReference<?> child);

  default <T> void addChild(int index, T child, TypeToken<T> type) {
    addChild(index, typedObject(type, child));
  }

  default <T> void addChild(int index, T child, Class<T> type) {
    addChild(index, child, forType(type));
  }

  @SuppressWarnings("unchecked")
  default void addChild(int index, Object child) {
    addChild(index, child, (Class<Object>) child.getClass());
  }

  default <T> void addChild(TypedReference<T> child) {
    addChild((int) getChildren().count(), child);
  }

  default <T> void addChild(T child, TypeToken<T> type) {
    addChild(typedObject(type, child));
  }

  default <T> void addChild(T child, Class<T> type) {
    addChild(child, forType(type));
  }

  @SuppressWarnings("unchecked")
  default void addChild(Object child) {
    addChild(child, (Class<Object>) child.getClass());
  }

  Stream<TypedReference<?>> getChildren();

  default boolean hasChildren() {
    return getChildren().findAny().isPresent();
  }

  default TypedReference<?> getChild(int index) {
    return getChildren().skip(index).findFirst().get();
  }

  void removeChild(int index);

  boolean removeChild(Object child);
}
