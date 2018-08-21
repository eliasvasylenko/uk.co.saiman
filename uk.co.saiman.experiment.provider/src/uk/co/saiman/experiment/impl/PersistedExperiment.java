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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import java.io.IOException;
import java.util.stream.Stream;

import uk.co.saiman.experiment.state.StateMap;

/**
 * implementations do not need to maintain identity or keep unique instances for
 * a given experiment node, but they do need to define equality.
 * 
 * @author Elias N Vasylenko
 *
 */
public interface PersistedExperiment {
  String getId();

  void setId(String id) throws IOException;

  String getTypeId();

  StateMap getPersistedState();

  Stream<? extends PersistedExperiment> getChildren();

  PersistedExperiment addChild(int index, String id, String typeId, StateMap configuration)
      throws IOException;

  void removeChild(String id, String typeId) throws IOException;

  @Override
  boolean equals(Object obj);

  @Override
  int hashCode();
}
