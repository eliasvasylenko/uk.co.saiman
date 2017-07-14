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

import uk.co.saiman.comms.Bit;

public class EventStatusRegister {
	@Bit(0)
	public boolean shortCircuitDetected;
	@Bit(1)
	public boolean driveOverTemperature;
	@Bit(2)
	public boolean overVoltage;
	@Bit(3)
	public boolean underVoltage;
	@Bit(4)
	public boolean motorTemperatureSensorActive;
	@Bit(5)
	public boolean encoderFeedbackError;
	@Bit(6)
	public boolean motorPhasingError;
	@Bit(7)
	public boolean currentOutputLimited;
	@Bit(8)
	public boolean voltageOutputLimited;
	@Bit(9)
	public boolean positiveLimitSwitchActive;
	@Bit(10)
	public boolean negativeLimitSwitchActive;
	@Bit(11)
	public boolean enableInputNotActive;
	@Bit(12)
	public boolean driveDisabledBySoftware;
	@Bit(13)
	public boolean attemptingMotorStop;
	@Bit(14)
	public boolean motorBrakeActivated;
	@Bit(15)
	public boolean pwmOutputsDisabled;
	@Bit(16)
	public boolean positiveSoftwareLimit;
	@Bit(17)
	public boolean negativeSoftwareLimit;
	@Bit(18)
	public boolean trackingError;
	@Bit(19)
	public boolean trackingWarning;
	@Bit(20)
	public boolean driveResetCondition;
	@Bit(21)
	public boolean positionValueOverflow;
	@Bit(22)
	public boolean driveFault;
	@Bit(23)
	public boolean velocityLimit;
	@Bit(24)
	public boolean accelerationLimit;
	@Bit(25)
	public boolean trackingErrorExceedsBounds;
	@Bit(26)
	public boolean homeSwitchActive;
	@Bit(27)
	public boolean motionActive;
	@Bit(28)
	public boolean velocityErrorExceedsBounds;
	@Bit(29)
	public boolean phaseUninitialized;
	@Bit(30)
	public boolean commandFault;
}
