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

public enum SaintCommandAddress {
	NULL(0x00, 0x00, 0x00, 0x00),

	LED_LAT(0x11),
	LED_PORT(0x12),

	VACUUM_LAT(0x21),
	VACUUM_PORT(0x22),

	HV_LAT(0x31),
	HV_PORT(0x32),

	MOTOR_LAT(0x41),
	MOTOR_PORT(0x42),

	VACUUM_RB_LAT(0x51),
	VACUUM_RB_PORT(0x52),

	HV_RB_LAT(0x61),
	HV_RB_PORT(0x62),

	SPARE_IO_LAT(0x71),
	SPARE_IO_PORT(0x72),

	HV_DAC_1(0x82, 0x81, 0x80),
	HV_DAC_2(0x92, 0x91, 0x90),
	HV_DAC_3(0xA2, 0xA1, 0xA0),
	HV_DAC_4(0xB2, 0xB1, 0xB0),
	CMOS_REF(0xC2, 0xC1, 0xC0),
	LASER_DETECT_REF(0xD0, 0xD1, 0xD2),

	PIRANI_READBACK_ADC(0xE0, 0xE1),
	MAGNETRON_READBACK_ADC(0xE2, 0xE3),

	SPARE_MON_1_ADC(0xE4, 0xE5),
	SPARE_MON_2_ADC(0xE6, 0xE7),
	SPARE_MON_3_ADC(0xE8, 0xE9),
	SPARE_MON_4_ADC(0xEA, 0xEB),

	CURRENT_READBACK_1_ADC(0xEC, 0xED),
	CURRENT_READBACK_2_ADC(0xEE, 0xEF),
	CURRENT_READBACK_3_ADC(0xF0, 0xF1),
	CURRENT_READBACK_4_ADC(0xF2, 0xF3),

	VOLTAGE_READBACK_1_ADC(0xF4, 0xF5),
	VOLTAGE_READBACK_2_ADC(0xF6, 0xF7),
	VOLTAGE_READBACK_3_ADC(0xF8, 0xF9),
	VOLTAGE_READBACK_4_ADC(0xFA, 0xFB),

	TURBO_CONTROL(0x01),
	TURBO_READBACKS(0x02, 0x03, 0x04, 0x05),

	DELAYED_EXTRACTION(0x15),
	BEAM_BLANKER(0x16);

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
