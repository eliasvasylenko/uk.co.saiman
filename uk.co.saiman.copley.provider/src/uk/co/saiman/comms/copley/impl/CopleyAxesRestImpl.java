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
 * This file is part of uk.co.saiman.copley.provider.
 *
 * uk.co.saiman.copley.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;

import uk.co.saiman.comms.copley.CopleyAxis;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.rest.CopleyAxesRest;
import uk.co.saiman.comms.copley.rest.CopleyVariablesRest;

public class CopleyAxesRestImpl implements CopleyAxesRest {
  private final CopleyNode node;

  public CopleyAxesRestImpl(CopleyNode node) {
    this.node = node;
  }

  public CopleyAxis findAxis(int axis) {
    return node
        .getAxes()
        .skip(axis)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find axis " + node + " / " + axis));
  }

  public AxisDTO getAxisInfo(CopleyAxis axis) {
    AxisDTO axisDto = new AxisDTO();

    axisDto.axisNumber = axis.getAxisNumber();
    axisDto.variables = new HashMap<>();

    return axisDto;
  }

  @Override
  public List<AxisDTO> getAxes() {
    return node.getAxes().map(this::getAxisInfo).collect(toList());
  }

  @Override
  public AxisDTO getAxis(int axis) {
    return getAxisInfo(findAxis(axis));
  }

  @Override
  public CopleyVariablesRest getVariables(int axis) {
    return new CopleyVariablesRestImpl(findAxis(axis));
  }
}
