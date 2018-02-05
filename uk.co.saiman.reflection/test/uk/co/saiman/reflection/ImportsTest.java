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
 * This file is part of uk.co.saiman.reflection.
 *
 * uk.co.saiman.reflection is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection;

import org.junit.Assert;
import org.junit.Test;

import uk.co.saiman.reflection.Imports;

@SuppressWarnings("javadoc")
public class ImportsTest {
	class InnerClass {
		class InnerInnerClass {}
	}

	@Test
	public void getInnerClassBySimpleNameWithImport() throws NoSuchMethodException, SecurityException {
		Class<?> innerClass = Imports.empty().withImport(InnerClass.class).getNamedClass(InnerClass.class.getSimpleName());

		Assert.assertEquals(InnerClass.class, innerClass);
	}

	@Test
	public void getInnerClassByOwnerSimpleNameWithPackageImport() throws NoSuchMethodException, SecurityException {
		Class<?> innerClass = Imports.empty().withPackageImport(ImportsTest.class.getPackage())
				.getNamedClass(ImportsTest.class.getSimpleName() + "." + InnerClass.class.getSimpleName());

		Assert.assertEquals(InnerClass.class, innerClass);
	}

	@Test
	public void getClassBySimpleNameWithPackageImport() throws NoSuchMethodException, SecurityException {
		Class<?> innerClass = Imports.empty().withPackageImport(ImportsTest.class.getPackage())
				.getNamedClass(ImportsTest.class.getSimpleName());

		Assert.assertEquals(ImportsTest.class, innerClass);
	}
}
