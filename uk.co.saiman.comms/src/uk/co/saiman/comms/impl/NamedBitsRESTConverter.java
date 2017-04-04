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

import static java.lang.Enum.valueOf;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.NamedBits;
import uk.co.saiman.comms.rest.CommandRESTConverter;

@Component
public class NamedBitsRESTConverter implements CommandRESTConverter {
	@Override
	public Object convertOutput(Object target, Map<String, Object> output) {
		if (target instanceof NamedBits<?>) {
			NamedBits<?> object = (NamedBits<?>) target;
			for (String item : output.keySet()) {
				object = withSet(object, item, output);
			}
			return object;
		}

		return null;
	}

	private <T extends Enum<T>> NamedBits<?> withSet(
			NamedBits<T> object,
			String item,
			Map<String, Object> output) {
		return object.withSet(valueOf(object.getBitClass(), item), (Boolean) output.get(item));
	}

	@Override
	public Map<String, Boolean> convertInput(Object input) {
		if (input instanceof NamedBits<?>) {
			return ((NamedBits<?>) input)
					.toMap()
					.entrySet()
					.stream()
					.collect(toMap(e -> e.getKey().name(), Entry::getValue));
		}

		return null;
	}
}
