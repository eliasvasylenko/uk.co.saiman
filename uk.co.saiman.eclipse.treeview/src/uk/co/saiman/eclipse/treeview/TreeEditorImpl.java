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

final class TreeEditorImpl<T> implements TreeEditor<T> {
  private final Runnable commitEdit;
  private Runnable editListener;
  private boolean editable;

  TreeEditorImpl(Runnable commitEdit) {
    this.commitEdit = commitEdit;
    editable = commitEdit != null;
  }

  @Override
  public void addEditListener(Runnable onEdit) {
    if (editListener == null) {
      editListener = onEdit;
    } else {
      editListener = () -> {
        editListener.run();
        onEdit.run();
      };
    }
  }

  @Override
  public boolean isEditable() {
    return editable;
  }

  @Override
  public boolean isEditing() {
    editable = true;
    return commitEdit != null;
  }

  @Override
  public void commitEdit() {
    if (editListener != null)
      editListener.run();
    commitEdit.run();
  }
}
