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

import static uk.co.saiman.reflection.Types.getErasedType;
import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;

import javafx.scene.layout.HBox;
import uk.co.saiman.reflection.token.TypeToken;

final class TreeContributionObjectSupplier extends PrimaryObjectSupplier {
  private final TreeEntry<?> entry;
  private final HBox node;
  private final TreeChildrenImpl children;
  private final TreeEditorImpl<?> editor;
  private final TreeClipboardManager dragAndDrop;

  TreeContributionObjectSupplier(
      TreeEntry<?> entry,
      HBox node,
      TreeChildrenImpl children,
      TreeEditorImpl<?> editor,
      TreeClipboardManager dragAndDrop) {
    this.entry = entry;
    this.node = node;
    this.children = children;
    this.editor = editor;
    this.dragAndDrop = dragAndDrop;
  }

  @Override
  public void resumeRecording() {}

  @Override
  public void pauseRecording() {}

  @Override
  public void get(
      IObjectDescriptor[] descriptors,
      Object[] actualValues,
      IRequestor requestor,
      boolean initial,
      boolean track,
      boolean group) {
    for (int i = 0; i < descriptors.length; i++) {
      Type desired = descriptors[i].getDesiredType();

      if (desired == TreeChildren.class) {
        actualValues[i] = children;

      } else if (desired == HBox.class) {
        actualValues[i] = node;

      } else if (desired instanceof ParameterizedType && getErasedType(desired) == TreeEntry.class
          && getTypeArgument(desired).isAssignableFrom(entry.type())) {
        actualValues[i] = entry;

      } else if (desired instanceof ParameterizedType && getErasedType(desired) == TreeEditor.class
          && getTypeArgument(desired).isAssignableFrom(entry.type())) {
        actualValues[i] = editor;

      } else if (desired instanceof ParameterizedType
          && getErasedType(desired) == TreeClipboard.class) {
        actualValues[i] = dragAndDrop.getForType(getTypeArgument(desired));
      }
    }
  }

  private TypeToken<?> getTypeArgument(Type desired) {
    return forType(desired).getTypeArguments().findFirst().get().getTypeToken();
  }
}