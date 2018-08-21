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

public enum CopleyOperationID {
	NO_OP(0x00),
	GET_OPERATING_MODE(0x07),
	GET_FLASH_CRC(0x0A),
	SWITCH_OPERATING_MODE(0x11),

	GET_VARIABLE(0x0C),
	GET_DEFAULT(0x0C),

	SET_VARIABLE(0x0D),
	SET_DEFAULT(0x0D),

	COPY_VARIABLE(0x0E),
	SAVE_VARIABLE(0x0E),
	LOAD_VARIABLE(0x0E),

	TRACE_VARIABLE(0x0F),
	RESET(0x10),
	TRAJECTORY(0x11),
	ERROR_LOG(0x12),
	COPLEY_VIRTUAL_MACHINE(0x14),
	ENCODER(0x1B),
	GET_CAN_OBJECT(0x1C),
	SET_CAN_OBJECT(0x1D),
	DYNAMIC_FILE_INTERFACE(0x21);

	private final byte code;

	private CopleyOperationID(int code) {
		this.code = (byte) code;
	}

	public byte getCode() {
		return code;
	}

	public static CopleyOperationID getCanonicalOperation(byte code) {
		for (CopleyOperationID operation : CopleyOperationID.values()) {
			if (operation.getCode() == code)
				return operation;
		}

		throw new IllegalArgumentException("Unknown code " + code);
	}
}
