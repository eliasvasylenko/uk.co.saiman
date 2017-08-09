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
 * This file is part of uk.co.saiman.reflection.resource.
 *
 * uk.co.saiman.reflection.resource is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection.resource is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection.resource;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Attribute {
	private final String name;
	private final Map<String, AttributeProperty<?>> properties;

	public Attribute(String name, AttributeProperty<?>... attributes) {
		this(name, Arrays.asList(attributes));
	}

	public Attribute(String name, Collection<? extends AttributeProperty<?>> attributes) {
		this.name = name;
		this.properties = Collections
				.unmodifiableMap(attributes.stream().collect(toMap(AttributeProperty::name, Function.identity())));
	}

	public String name() {
		return name;
	}

	public Map<String, AttributeProperty<?>> properties() {
		return properties;
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ properties.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Attribute)) {
			return false;
		}

		Attribute otherAttribute = (Attribute) obj;

		return Objects.equals(name, otherAttribute.name()) && Objects.equals(properties, otherAttribute.properties());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(name);

		for (AttributeProperty<?> property : properties.values()) {
			builder.append(";").append(property);
		}

		return builder.toString();
	}
}
