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
 * This file is part of uk.co.saiman.instrument.raster.
 *
 * uk.co.saiman.instrument.raster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.raster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.instrument.raster;

import static uk.co.strangeskies.utility.Enumeration.readableName;

import java.util.Iterator;

public interface RasterPattern {
	enum RasterPatterns implements RasterPattern {
		SNAKE {
			@Override
			public Iterator<RasterPosition> getPositionIterator(int width, int height) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Iterator<RasterPosition> getReversePositionIterator(int width, int height) {
				// TODO Auto-generated method stub
				return null;
			}
		},

		SPIRAL {
			@Override
			public Iterator<RasterPosition> getPositionIterator(int width, int height) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Iterator<RasterPosition> getReversePositionIterator(int width, int height) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		@Override
		public String getName() {
			return readableName(this);
		}
	}

	String getName();

	Iterator<RasterPosition> getPositionIterator(int width, int height);

	Iterator<RasterPosition> getReversePositionIterator(int width, int height);
}
