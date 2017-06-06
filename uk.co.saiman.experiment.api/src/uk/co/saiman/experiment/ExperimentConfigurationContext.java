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
package uk.co.saiman.experiment;

/**
 * The context of an experiment node's initial configuration. When a workspace
 * is requested to create an experiment node of a given type, this context is
 * instantiated and passed to the experiment type implementation via
 * {@link ExperimentType#createState(ExperimentConfigurationContext)}.
 * <p>
 * In other words, each {@link ExperimentNode} has only one
 * {@link ExperimentConfigurationContext} associated with it when it is created.
 * The configuration context remains valid so long as the experiment node
 * remains in the workspace, and references may be held to it.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the executing node
 */
public interface ExperimentConfigurationContext<T> {
	/**
	 * @return the currently executing experiment node
	 */
	ExperimentNode<?, T> node();

	ResultManager results();

	/**
	 * This map represents the state of the experiment node associated with this
	 * configuration context. This data should be persisted by the workspace
	 * according to the format of an experiment file.
	 * <p>
	 * The map is coupled directly with the persisted data, with changes of the
	 * map being immediately stored.
	 * <p>
	 * There is no standard enforced for the format of the value strings.
	 * <p>
	 * The execution of an experiment should generally not affect its persisted
	 * state, directly or otherwise.
	 * 
	 * @return a map containing persisted key/value pairs
	 */
	PersistedState persistedState();

	/**
	 * Set the ID of the node. The ID should be unique amongst the children of a
	 * node's parent. Typically the ID may be used to locate the
	 * {@link #persistedState() persisted state} of an experiment, and so
	 * changing the ID may result in the movement or modification of persisted
	 * data.
	 * 
	 * @param id
	 *          the ID for the node
	 */
	void setID(String id);
}
