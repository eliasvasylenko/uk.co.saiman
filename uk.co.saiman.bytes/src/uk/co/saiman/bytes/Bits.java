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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * For annotating fields of a class representing a comms data packet. This
 * annotation declares the number of contiguous bits represented by the field,
 * and the class used to convert the field.
 * <p>
 * To specify the bit position of the field in the data packet, use the
 * {@link Bit} annotation.
 * 
 * @author Elias N Vasylenko
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Bits {
	/**
	 * The bit size of the data. If unspecified, the default is given by the
	 * converter class used.
	 * 
	 * @return the size of the data, or a negative number to denote unspecified.
	 */
	int value() default -1;

	/**
	 * The factory used to find a converter for the field. If unspecified, the
	 * default is determined automatically according to the type of the field.
	 * 
	 * @return the factory to use, or {@link BitConverterFactory} to denote
	 *         unspecified.
	 */
	Class<? extends BitConverterFactory> converter() default BitConverterFactory.class;
}
