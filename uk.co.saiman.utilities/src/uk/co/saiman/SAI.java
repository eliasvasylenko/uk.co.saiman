/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman;

import java.time.LocalDate;

import uk.co.strangeskies.modabi.Namespace;

/**
 * Utilities relating conceptually to the Scientific Analysis Instruments set of
 * products.
 * 
 * @author Elias N Vasylenko
 */
public class SAI {
	private SAI() {}

	/**
	 * The root namespace associated with Scientific Analysis Instruments products
	 */
	public static final Namespace NAMESPACE = new Namespace(SAI.class.getPackage(), LocalDate.of(2015, 10, 8));
}
