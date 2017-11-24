/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.path;

import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentPathTest;

/**
 * Currently the {@link ExperimentPathTest path} only matches each child by ID.
 * This could be expanded to XPath like behavior with different matching
 * strategies.
 */
public class ExperimentMatcher {
  private final String id;

  ExperimentMatcher(String id) {
    this.id = id;
  }

  public static ExperimentMatcher matching(ExperimentNode<?, ?> node) {
    return new ExperimentMatcher(node.getId());
  }

  public boolean match(ExperimentNode<?, ?> node) {
    return id.equals(node.getId());
  }

  @Override
  public String toString() {
    return id;
  }

  public static ExperimentMatcher fromString(String string) {
    return new ExperimentMatcher(string);
  }
}
