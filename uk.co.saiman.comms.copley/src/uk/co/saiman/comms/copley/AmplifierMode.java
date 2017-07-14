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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley;

public enum AmplifierMode {
	DRIVE_DISABLED(0),
	CURRENT_LOOP_BY_PROGRAMMED_VALUE(1),
	CURRENT_LOOP_BY_ANALOG_REFERENCE(2),
	CURRENT_LOOP_BY_PWM(3),
	CURRENT_LOOP_BY_FUNCTION_GENERATOR(4),
	UV_CURRENT_MODE(5),

	VELOCITY_LOOP_BY_PROGRAMMED_VALUE(11),
	VELOCITY_LOOP_BY_ANALOG_REFERENCE(12),
	VELOCITY_LOOP_BY_PWM(13),
	VELOCITY_LOOP_BY_FUNCTION_GENERATOR(14),

	POSITION_LOOP_BY_TRAJECTORY_GENERATOR(21),
	POSITION_LOOP_BY_ANALOG_REFERENCE(22),
	POSITION_LOOP_BY_DIGITAL_INPUT_LINES(23),
	POSITION_LOOP_BY_FUNCTION_GENERATOR(24),
	POSITION_LOOP_BY_CAM_TABLES(25),

	DRIVE_CONTROL_BY_CANOPEN_OR_ETHERCAT(30),
	MICROSTEPPER_BY_TRAJECTORY_GENERATOR(31),
	MICROSTEPPER_BY_DIGITAL_INPUT_LINES(33),
	MICROSTEPPER_BY_FUNCTION_GENERATOR(34),
	MICROSTEPPER_BY_CAM_TABLES(35),
	MICROSTEPPER_BY_CANOPEN_OR_ETHERCAT(40),

	MICROSTEPPER_SIMPLE_DIAGNOSTIC_MODE(42);

	private final int code;

	private AmplifierMode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static AmplifierMode forCode(int code) {
		for (AmplifierMode command : values())
			if (command.getCode() == code)
				return command;

		throw new IllegalArgumentException();
	}
}
