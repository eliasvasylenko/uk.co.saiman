/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.instrument.vacuum.
 *
 * uk.co.saiman.instrument.vacuum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.vacuum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.vacuum;

/**
 * This exception represents a problem with an acquisition device or process.
 * 
 * @author Elias N Vasylenko
 */
public class VacuumException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *          a description of the problem
	 */
	public VacuumException(String message) {
		super(message);
	}

	/**
	 * @param message
	 *          a description of the problem
	 * @param cause
	 *          the causing throwable
	 */
	public VacuumException(String message, Throwable cause) {
		super(message, cause);
	}
}
