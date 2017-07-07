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
package uk.co.saiman.comms.copley.impl;

import uk.co.saiman.comms.Bits;

public class EventStatusRegister {
	@Bits(0)
	public boolean shortCircuitDetected;
	@Bits(1)
	public boolean driveOverTemperature;
	@Bits(2)
	public boolean overVoltage;
	@Bits(3)
	public boolean underVoltage;
	@Bits(4)
	public boolean motorTemperatureSensorActive;
	@Bits(5)
	public boolean encoderFeedbackError;
	@Bits(6)
	public boolean motorPhasingError;
	@Bits(7)
	public boolean currentOutputLimited;
	@Bits(8)
	public boolean voltageOutputLimited;
	@Bits(9)
	public boolean positiveLimitSwitchActive;
	@Bits(10)
	public boolean negativeLimitSwitchActive;
	@Bits(11)
	public boolean enableInputNotActive;
	@Bits(12)
	public boolean driveDisabledBySoftware;
	@Bits(13)
	public boolean attemptingMotorStop;
	@Bits(14)
	public boolean motorBrakeActivated;
	@Bits(15)
	public boolean pwmOutputsDisabled;
	@Bits(16)
	public boolean positiveSoftwareLimit;
	@Bits(17)
	public boolean negativeSoftwareLimit;
	@Bits(18)
	public boolean trackingError;
	@Bits(19)
	public boolean trackingWarning;
	@Bits(20)
	public boolean driveResetCondition;
	@Bits(21)
	public boolean positionValueOverflow;
	@Bits(22)
	public boolean driveFault;
	@Bits(23)
	public boolean velocityLimit;
	@Bits(24)
	public boolean accelerationLimit;
	@Bits(25)
	public boolean trackingErrorExceedsBounds;
	@Bits(26)
	public boolean homeSwitchActive;
	@Bits(27)
	public boolean motionActive;
	@Bits(28)
	public boolean velocityErrorExceedsBounds;
	@Bits(29)
	public boolean phaseUninitialized;
	@Bits(30)
	public boolean commandFault;
}
