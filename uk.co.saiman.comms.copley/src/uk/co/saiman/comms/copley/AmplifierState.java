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
package uk.co.saiman.comms.copley;

import java.lang.reflect.Type;

import uk.co.saiman.comms.Bit;
import uk.co.saiman.comms.BitConverter;
import uk.co.saiman.comms.BitConverterFactory;
import uk.co.saiman.comms.Bits;
import uk.co.saiman.comms.Bytes;
import uk.co.saiman.comms.PrimitiveBitConverters;

@Bytes(2)
public class AmplifierState {
	@Bit(0)
	@Bits(converter = AmplifierModeConverter.class)
	public AmplifierMode mode;
}

class AmplifierModeConverter implements BitConverterFactory {
	@Override
	public BitConverter<AmplifierMode> getBitConverter(Type type) {
		return new PrimitiveBitConverters.Ints()
				.map(AmplifierMode::forCode, AmplifierMode::getCode)
				.withDefaultBits(16);
	}
}
