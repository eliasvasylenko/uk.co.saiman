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
package uk.co.saiman.instrument.raster;

import java.util.Iterator;

public interface RasterMode {
	public static final RasterMode SNAKE = null;
	public static final RasterMode SPIRAL = null;

	String getName();

	Iterator<RasterPosition> getPositionIterator(int width, int height);

	Iterator<RasterPosition> getReversePositionIterator(int width, int height);
}