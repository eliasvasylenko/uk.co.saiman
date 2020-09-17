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
 * This file is part of uk.co.saiman.copley.
 *
 * uk.co.saiman.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley;

import java.util.Optional;
import java.util.stream.Stream;

public interface CopleyController {
  int HEADER_SIZE = 4;
  byte CHECKSUM = 0x5A;
  int WORD_SIZE = 2;

  /**
   * Attempt to reset the comms is they are in an error state. If everything is
   * working, invocation does nothing.
   */
  void reset();

  Stream<CopleyNode> getNodes();

  default Optional<CopleyNode> getNode(int id) {
    return getNodes().filter(node -> node.getId() == id).findAny();
  }

  default Optional<CopleyAxis> getAxis(int nodeId, int axisNumber) {
    return getNode(nodeId).flatMap(node -> node.getAxis(axisNumber));
  }
}
