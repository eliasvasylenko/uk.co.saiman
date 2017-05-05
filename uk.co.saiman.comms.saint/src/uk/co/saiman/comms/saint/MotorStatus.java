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

import java.util.HashMap;

import uk.co.saiman.comms.Bits;

public class MotorStatus {
	@Bits(0)
	public boolean lockMotorBreak;
	@Bits(1)
	public boolean lockMotorPhase;
	@Bits(2)
	public boolean lockMotorMode;
	@Bits(3)
	public boolean lockMotorEnable;
	@Bits(4)
	public boolean lockFullyOpen;
	@Bits(5)
	public boolean lockFullyClosed;
	@Bits(6)
	public boolean lockOpen;
	@Bits(7)
	public boolean lockClose;

	@Override
	public String toString() {
		return new HashMap<String, Boolean>() {
			{
				put("lockMotorBreak", lockMotorBreak);
				put("lockMotorPhase", lockMotorPhase);
				put("lockMotorMode", lockMotorMode);
				put("lockMotorEnable", lockMotorEnable);
				put("lockFullyOpen", lockFullyOpen);
				put("lockFullyClosed", lockFullyClosed);
				put("lockOpen", lockOpen);
				put("lockClose", lockClose);
			}
		}.toString();
	}
}
