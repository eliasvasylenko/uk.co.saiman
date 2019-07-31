/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.graph.
 *
 * uk.co.saiman.experiment.graph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.graph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * An experimental procedure which may be conducted according to this API takes
 * the form of a graph.
 * <p>
 * An experiment graph is a tree. The root vertex represents an entire
 * self-contained experiment procedure, and every other vertex represents a step
 * to be taken as part of that procedure.
 * <p>
 * {@link uk.co.saiman.experiment.declaration.ExperimentRelation Edges} of the graph
 * define a hierarchy of ownership and dependency between vertices. An incoming
 * edge leads to a dependency, and an outgoing edge leads to a dependent. A
 * vertex is said to own its dependent steps. Each step must have exactly one
 * owner.
 * <p>
 * Each experiment vertex has an
 * {@link uk.co.saiman.experiment.declaration.ExperimentId id}, and each edge should
 * be labeled with the id of its dependent. Each vertex id need not be globally
 * unique, but no vertex should own more than one dependent step with the same
 * id.
 */
@org.osgi.annotation.versioning.Version("1.0.0")
package uk.co.saiman.experiment.declaration;
