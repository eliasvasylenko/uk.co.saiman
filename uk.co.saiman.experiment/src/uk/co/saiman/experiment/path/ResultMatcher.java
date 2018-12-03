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
package uk.co.saiman.experiment.path;

import uk.co.saiman.experiment.Observation;
import uk.co.saiman.experiment.Result;

/**
 * Currently the {@link ResultMatcher path} only matches each child by ID. This
 * could be expanded to e.g. XPath-like behavior with different matching
 * strategies.
 */
public class ResultMatcher<T> {
  private final String id;

  ResultMatcher(String id) {
    this.id = id;
  }

  public static <T> ResultMatcher<T> matching(Observation<T> observation) {
    return new ResultMatcher<>(observation.id());
  }

  public boolean match(Result<?> result) {
    return id.equals(result.getObservation().id());
  }

  @Override
  public String toString() {
    return id;
  }

  public static ResultMatcher<?> fromString(String string) {
    return new ResultMatcher<>(string);
  }
}
