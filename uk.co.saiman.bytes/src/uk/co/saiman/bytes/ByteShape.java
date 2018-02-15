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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.bytes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ByteShape {
	private final List<String> bits;

	public ByteShape(
			String bit0,
			String bit1,
			String bit2,
			String bit3,
			String bit4,
			String bit5,
			String bit6,
			String bit7) {
		bits = new ArrayList<>(8);
		bits.add(bit0);
		bits.add(bit1);
		bits.add(bit2);
		bits.add(bit3);
		bits.add(bit4);
		bits.add(bit5);
		bits.add(bit6);
		bits.add(bit7);
	}

	public Stream<String> getBits() {
		return bits.stream();
	}
}
