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

public class SaiUiEvents {
  private SaiUiEvents() {}

  public class Cell {
    private Cell() {}

    public static final String TOPIC_POPUP_MENU = "org/eclipse/e4/ui/model/ui/Cell/popupMenu/*";

    public static final String TOPIC_EXPANDED = "org/eclipse/e4/ui/model/ui/Cell/expanded/*";
    public static final String EXPANDED = "expanded";

    public static final String TOPIC_NULLABLE = "org/eclipse/e4/ui/model/ui/Cell/nullable/*";
    public static final String NULLABLE = "nullable";

    public static final String TOPIC_CONTEXT_VALUE = "org/eclipse/e4/ui/model/ui/Cell/contextValue/*";
    public static final String CONTEXT_VALUE = "contextValue";
  }

  public class EditableCell {
    public static final String TOPIC_EDITING = "org/eclipse/e4/ui/model/ui/EditableCell/editing/*";
    public static final String EDITING = "editing";
  }

  public class Tree {
    private Tree() {}

    public static final String TOPIC_EDITABLE = "org/eclipse/e4/ui/model/ui/Cell/editable/*";
    public static final String EDITABLE = "editable";
  }
}
