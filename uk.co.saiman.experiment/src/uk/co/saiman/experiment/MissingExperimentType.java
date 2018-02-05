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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.collection.StreamUtilities.mapToEntry;
import static uk.co.saiman.experiment.ExperimentNodeConstraint.ASSUME_ALL_FULFILLED;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.reflection.token.TypeToken;

public interface MissingExperimentType<T> extends ExperimentType<Map<String, Object>, T> {
  @Override
  default Type getThisType() {
    return MissingExperimentType.class;
  }

  @Override
  default String getId() {
    return MissingExperimentType.class.getName();
  }

  String getMissingTypeID();

  @Override
  default Map<String, Object> createState(ConfigurationContext<Map<String, Object>> context) {
    return persistedStateMap(context.persistedState());
  }

  default Map<String, Object> persistedStateMap(PersistedState persistedState) {
    return new AbstractMap<String, Object>() {
      @Override
      public Set<Entry<String, Object>> entrySet() {
        return Stream
            .of(
                persistedState
                    .getStrings()
                    .map(mapToEntry(s -> (Object) persistedState.forString(s).get())),
                persistedState
                    .getMaps()
                    .map(mapToEntry(s -> (Object) persistedStateMap(persistedState.getMap(s)))),
                persistedState
                    .getMapLists()
                    .map(
                        mapToEntry(
                            s -> (Object) persistedState
                                .getMapList(s)
                                .stream()
                                .map(e -> persistedStateMap(e))
                                .collect(toList()))))
            .flatMap(v -> v)
            .collect(toSet());
      }
    };
  }

  @Override
  default ExperimentNodeConstraint mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return ASSUME_ALL_FULFILLED;
  }

  @Override
  default ExperimentNodeConstraint mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    return ASSUME_ALL_FULFILLED;
  }

  @Override
  default TypeToken<Map<String, Object>> getStateType() {
    return new TypeToken<Map<String, Object>>() {};
  }

  @Override
  TypeToken<T> getResultType();
}
