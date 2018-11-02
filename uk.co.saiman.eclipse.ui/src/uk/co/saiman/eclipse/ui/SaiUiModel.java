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

import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SaiUiModel {
  private SaiUiModel() {}

  /**
   * The primary context key for a {@link MUIElement UI element}, to be set on the
   * {@link MContext#getProperties() context properties} of the element. If the
   * primary key is not present in the context of the element then the element may
   * be {@link #HIDE_ON_NULL automatically hidden} or
   * {@link EPartService#REMOVE_ON_HIDE_TAG removed}.
   * <p>
   * The primary key may also be used by other services which expect to be have a
   * value associated with a UI element, e.g. for copy and paste or drag and drop
   * transfers.
   */
  public static final String PRIMARY_CONTEXT_KEY = "primaryContextKey";
  public static final String NULLABLE = "nullable";
  public static final String HIDE_ON_NULL = "hideOnNull";

  public static final String TRANSFER_MEDIA_TYPE = "transferMediaType";
  public static final String TRANSFER_FORMAT = "transferFormat";

  public static final String EDIT_CANCELED = "editCancelled";
}
