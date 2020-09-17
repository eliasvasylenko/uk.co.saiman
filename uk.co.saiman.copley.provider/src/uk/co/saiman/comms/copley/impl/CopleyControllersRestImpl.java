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

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.copley.CopleyNode;
import uk.co.saiman.comms.copley.rest.CopleyControllersRest;
import uk.co.saiman.comms.copley.rest.CopleyNodesRest;

public class CopleyControllersRestImpl implements CopleyControllersRest {
  private final CopleyService service;

  public CopleyControllersRestImpl(CopleyService service) {
    this.service = service;
  }

  private BundleDTO getBundleInfo(Bundle bundle) {
    return bundle.adapt(BundleDTO.class);
  }

  public ControllerDTO getControllerInfo(CopleyController controller) {
    controller.reset();

    ControllerDTO controllerDto = new ControllerDTO();

    controllerDto.id = service.getId(controller);
    controllerDto.nodes = controller.getNodes().mapToInt(CopleyNode::getId).toArray();
    controllerDto.bundle = getBundleInfo(service.getBundle(controller));

    return controllerDto;
  }

  @Override
  public List<ControllerDTO> getControllers() {
    return service.getControllers().map(this::getControllerInfo).collect(toList());
  }

  @Override
  public ControllerDTO getController(String name) {
    return getControllerInfo(service.getController(name));
  }

  @Override
  public CopleyNodesRest getNodes(String name) {
    return new CopleyNodesRestImpl(service.getController(name));
  }
}
