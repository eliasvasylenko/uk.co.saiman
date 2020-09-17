/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

public class SaiUiModel {
  private SaiUiModel() {}

  public static final String EAGER_INIT = "eagerInitialization";

  public static final String TRANSFER_MEDIA_TYPE = "transferMediaType";
  public static final String TRANSFER_FORMAT = "transferFormat";

  public static final String EDIT_CANCELED = "editCancelled";

  /*
   * TODO alternative to our "primaryContextKey" system, which is kinda arcane.
   * Better to express the concept in code rather than some magic tags & context
   * values on the model items!
   * 
   * TODO Explore whether drag and drop can be done in a similar way, as that is
   * also done with primaryContextKey currently! I expect it can. Much simpler and
   * better.
   * 
   * @StartDrag returns the draggable item
   * 
   * @DropOver @DropBefore @DropAfter perform the appropriate actions. Fantastic!
   * Way better than the awkward TransferCellHandler shit.
   * 
   * TODO can we replace the ChildrenService in the same way? A specially
   * annotated method simply returns a list of objects. Is automatically
   * reinjected any time one of the things it was injected with changes.
   * 
   * @GenerageChildren("id.of.child.node")
   * 
   */
}
