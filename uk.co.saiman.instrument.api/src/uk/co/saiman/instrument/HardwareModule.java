/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.instrument.api.
 *
 * uk.co.saiman.instrument.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument;

import java.util.function.Consumer;

public interface HardwareModule {
	String getName();

	/**
	 * @return True if a communication link with the hardware properly
	 *         established, false otherwise
	 */
	boolean isConnected();

	void reset();

	/**
	 * If the hardware module is performing some sort of operation, e.g. a
	 * currently running raster or acquisition card, that operation should be
	 * aborted, or for high voltages they should be turned off if possible.
	 */
	void abortOperation();

	void addErrorListener(Consumer<Exception> exception);
}
