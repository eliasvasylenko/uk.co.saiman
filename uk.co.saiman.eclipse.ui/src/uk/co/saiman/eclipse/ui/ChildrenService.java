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
 * This file is part of uk.co.saiman.eclipse.ui.
 *
 * uk.co.saiman.eclipse.ui is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface ChildrenService {
  default <T> void setItem(String modelElementId, Class<T> contextClass, T child) {
    setItem(modelElementId, contextClass.getName(), child);
  }

  default <T> void setItem(
      String modelElementId,
      Class<T> contextClass,
      T child,
      Consumer<? super T> update) {
    setItem(modelElementId, contextClass.getName(), child, update);
  }

  default <T> void setItems(
      String modelElementId,
      Class<T> contextClass,
      Collection<? extends T> children) {
    setItems(modelElementId, contextClass.getName(), children);
  }

  default <T> void setItems(
      String modelElementId,
      Class<T> contextClass,
      List<? extends T> children,
      Consumer<? super List<? extends T>> update) {
    setItems(modelElementId, contextClass.getName(), children, update);
  }

  <T> void setItem(String modelElementId, String contextName, T child);

  <T> void setItem(String modelElementId, String contextName, T child, Consumer<? super T> update);

  <T> void setItems(String modelElementId, String contextName, Collection<? extends T> children);

  <T> void setItems(
      String modelElementId,
      String contextName,
      List<? extends T> children,
      Consumer<? super List<? extends T>> update);
}
