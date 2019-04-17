/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.copley.
 *
 * uk.co.saiman.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley;

import uk.co.saiman.bytes.conversion.Offset;

public class EventStatusRegister {
	@Offset(0)
	public boolean shortCircuitDetected;
	@Offset(1)
	public boolean driveOverTemperature;
	@Offset(2)
	public boolean overVoltage;
	@Offset(3)
	public boolean underVoltage;
	@Offset(4)
	public boolean motorTemperatureSensorActive;
	@Offset(5)
	public boolean encoderFeedbackError;
	@Offset(6)
	public boolean motorPhasingError;
	@Offset(7)
	public boolean currentOutputLimited;
	@Offset(8)
	public boolean voltageOutputLimited;
	@Offset(9)
	public boolean positiveLimitSwitchActive;
	@Offset(10)
	public boolean negativeLimitSwitchActive;
	@Offset(11)
	public boolean enableInputNotActive;
	@Offset(12)
	public boolean driveDisabledBySoftware;
	@Offset(13)
	public boolean attemptingMotorStop;
	@Offset(14)
	public boolean motorBrakeActivated;
	@Offset(15)
	public boolean pwmOutputsDisabled;
	@Offset(16)
	public boolean positiveSoftwareLimit;
	@Offset(17)
	public boolean negativeSoftwareLimit;
	@Offset(18)
	public boolean trackingError;
	@Offset(19)
	public boolean trackingWarning;
	@Offset(20)
	public boolean driveResetCondition;
	@Offset(21)
	public boolean positionValueOverflow;
	@Offset(22)
	public boolean driveFault;
	@Offset(23)
	public boolean velocityLimit;
	@Offset(24)
	public boolean accelerationLimit;
	@Offset(25)
	public boolean trackingErrorExceedsBounds;
	@Offset(26)
	public boolean homeSwitchActive;
	@Offset(27)
	public boolean motionActive;
	@Offset(28)
	public boolean velocityErrorExceedsBounds;
	@Offset(29)
	public boolean phaseUninitialized;
	@Offset(30)
	public boolean commandFault;
}
