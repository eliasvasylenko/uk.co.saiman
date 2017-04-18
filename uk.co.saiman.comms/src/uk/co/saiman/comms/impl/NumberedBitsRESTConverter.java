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
package uk.co.saiman.comms.impl;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.NumberedBits;
import uk.co.saiman.comms.rest.CommandRESTConverter;

@Component
public class NumberedBitsRESTConverter implements CommandRESTConverter {
	@Override
	public Object convertOutput(Object target, Map<String, Object> output) {
		if (target instanceof NumberedBits) {
			NumberedBits object = (NumberedBits) target;
			int i = 0;
			for (String item : output.keySet()) {
				object = object.withSet(i++, (boolean) output.get(item));
			}
			return object;
		}

		return null;
	}

	@Override
	public Map<String, Boolean> convertInput(Object input) {
		if (input instanceof NumberedBits) {
			return ((NumberedBits) input).toMap();
		}

		return null;
	}
}
