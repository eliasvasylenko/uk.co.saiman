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
 * but WITHOUT ANY WARRANTY(); without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.Comms;

public interface SaintComms extends Comms {
	/**
	 * The id of the SAINT comms interface.
	 */
	String ID = "SAINT Comms";
	/**
	 * The message size in bytes for the SAINT comms interface, for both sending
	 * and receiving messages.
	 */
	int MESSAGE_SIZE = 4;

	Value<LEDStatus> statusLED();

	Value<VacuumControl> vacuum();

	ValueRequest<HighVoltageStatus> highVoltage();

	Value<MotorStatus> motorStatus();

	Value<VacuumReadback> vacuumReadback();

	Value<HighVoltageReadback> highVoltageReadback();

	ValueRequest<I2C> highVoltageDAC1();

	ValueRequest<I2C> highVoltageDAC2();

	ValueRequest<I2C> highVoltageDAC3();

	ValueRequest<I2C> highVoltageDAC4();

	ValueRequest<I2C> cmosRef();

	ValueRequest<I2C> laserDetectRef();

	ValueReadback<ADC> piraniReadback();

	ValueReadback<ADC> magnetronReadback();

	ValueReadback<ADC> spareMon1();

	ValueReadback<ADC> spareMon2();

	ValueReadback<ADC> spareMon3();

	ValueReadback<ADC> spareMon4();

	ValueReadback<ADC> currentReadback1();

	ValueReadback<ADC> currentReadback2();

	ValueReadback<ADC> currentReadback3();

	ValueReadback<ADC> currentReadback4();

	ValueReadback<ADC> voltageReadback1();

	ValueReadback<ADC> voltageReadback2();

	ValueReadback<ADC> voltageReadback3();

	ValueReadback<ADC> voltageReadback4();

	ValueRequest<TurboControl> turboControl();

	ValueReadback<TurboReadbacks> turboReadbacks();
}
