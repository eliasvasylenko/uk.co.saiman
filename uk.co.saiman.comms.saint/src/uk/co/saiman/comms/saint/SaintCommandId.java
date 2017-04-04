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
 * This file is part of uk.co.saiman.comms.saint.
 *
 * uk.co.saiman.comms.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.saint;

public class SaintCommandId {
	public enum SaintCommandType {
		PING(0x00), INPUT(0xA0), OUTPUT(0xAF);

		private final byte type;

		private SaintCommandType(int address) {
			this.type = (byte) address;
		}

		public byte getByte() {
			return type;
		}
	}

	public enum SaintCommandAddress {
		NULL(0x00, 0x00, 0x00, 0x00),

		LED_LAT(0x11),
		LED_PORT(0x12),

		VACUUM_LAT(0x21),
		VACUUM_PORT(0x22),

		HV_LAT(0x31),
		HV_PORT(0x32),

		STAGE_LAT(0x41),
		STAGE_PORT(0x42),

		HV_DAC_1(0x80, 0x81);

		private final byte[] address;

		private SaintCommandAddress(int... address) {
			this.address = new byte[address.length];
			for (int i = 0; i < address.length; i++) {
				this.address[i] = (byte) address[i];
			}
		}

		public byte[] getBytes() {
			return address;
		}

		public int getSize() {
			return address.length;
		}
	}

	private static final String SPLIT_CHARACTER = "::";

	private final SaintCommandType type;
	private final SaintCommandAddress address;

	public SaintCommandId(SaintCommandType type, SaintCommandAddress address) {
		this.type = type;
		this.address = address;
	}

	public SaintCommandType getType() {
		return type;
	}

	public SaintCommandAddress getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return address + SPLIT_CHARACTER + type;
	}
}
