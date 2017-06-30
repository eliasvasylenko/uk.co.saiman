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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.rest;

public interface CommsRESTAction {
	public enum Behaviour {
		/**
		 * Indicates that the action receives user-readable
		 * {@link CommsRESTEntry#getInputData() input data} through the comms
		 * interface.
		 */
		RECEIVES_INPUT_DATA,

		/**
		 * Indicates that the action sends the user-editable
		 * {@link CommsRESTEntry#getOutputData() output data} through the comms
		 * interface.
		 */
		SENDS_OUTPUT_DATA,

		/**
		 * Indicates that the action modifies the user-editable
		 * {@link CommsRESTEntry#getOutputData() output data} which may be sent
		 * through the comms interface.
		 */
		MODIFIES_OUTPUT_DATA,

		/**
		 * Indicates that the action may be polled.
		 */
		POLLABLE
	}

	String getID();

	boolean hasBehaviour(Behaviour behaviour);

	void invoke(String entry) throws Exception;
}
