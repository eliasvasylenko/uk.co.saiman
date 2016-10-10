/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
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
 * General configuration interface for experiment root nodes, as created via
 * {@link ExperimentWorkspace#addRootExperiment(String)}
 * 
 * @author Elias N Vasylenko
 */
public interface ExperimentConfiguration {
	/**
	 * @return the name of the experiment
	 */
	String getName();

	/**
	 * @param name
	 *          the new name for the experiment
	 */
	void setName(String name);

	/**
	 * @return the notes of the experiment
	 */
	String getNotes();

	/**
	 * @param notes
	 *          the new notes for the experiment
	 */
	void setNotes(String notes);

	public static boolean isNameValid(String name) {
		final String ALPHANUMERIC = "[a-zA-Z0-9]+";
		final String DIVIDER_CHARACTERS = "[ \\.\\-_]+";

		return name.matches(ALPHANUMERIC + "(" + DIVIDER_CHARACTERS + ALPHANUMERIC + ")*");
	}
}
