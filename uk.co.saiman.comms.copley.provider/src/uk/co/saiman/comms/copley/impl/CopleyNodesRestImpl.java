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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.rest.CopleyAxesRest;
import uk.co.saiman.comms.copley.rest.CopleyNodesRest;

public class CopleyNodesRestImpl implements CopleyNodesRest {
  private final CopleyController controller;

  public CopleyNodesRestImpl(CopleyController controller) {
    this.controller = controller;
  }

  public CopleyNode findNode(int node) {
    return controller
        .getNodes()
        .skip(node)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Cannot find node " + controller + " / " + node));
  }

  public NodeDTO getNodeInfo(CopleyNode node) {
    NodeDTO nodeDto = new NodeDTO();

    nodeDto.id = node.getId();
    nodeDto.operatingMode = node.getOperatingMode();
    nodeDto.axes = node.getAxes().mapToInt(CopleyAxis::getAxisNumber).toArray();

    return nodeDto;
  }

  @Override
  public List<NodeDTO> getNodes() {
    return controller.getNodes().map(this::getNodeInfo).collect(toList());
  }

  @Override
  public NodeDTO getNode(int node) {
    return getNodeInfo(findNode(node));
  }

  @Override
  public CopleyAxesRest getAxes(int node) {
    return new CopleyAxesRestImpl(findNode(node));
  }
}
