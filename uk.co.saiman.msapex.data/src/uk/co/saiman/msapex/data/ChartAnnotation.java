/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.data.
 *
 * uk.co.saiman.msapex.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.data;

import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

public interface ChartAnnotation<T> extends Reified<ChartAnnotation<T>> {
	T getData();

	TypeToken<T> getDataType();

	@Override
	default TypeToken<ChartAnnotation<T>> getThisType() {
		return new TypeToken<ChartAnnotation<T>>() {}.withTypeArgument(new TypeParameter<T>() {}, getDataType());
	}

	@Override
	default ChartAnnotation<T> getThis() {
		return this;
	}

	double getX();

	double getY();
}
