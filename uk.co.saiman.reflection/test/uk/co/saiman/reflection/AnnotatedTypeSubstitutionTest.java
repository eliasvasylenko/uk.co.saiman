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

import static java.util.Arrays.asList;
import static uk.co.saiman.reflection.AnnotatedArrayTypes.arrayFromComponent;
import static uk.co.saiman.reflection.AnnotatedParameterizedTypes.parameterize;
import static uk.co.saiman.reflection.AnnotatedTypes.annotated;
import static uk.co.saiman.reflection.AnnotatedWildcardTypes.wildcardExtending;
import static uk.co.saiman.reflection.AnnotatedWildcardTypes.wildcardSuper;
import static uk.co.saiman.reflection.Annotations.from;
import static uk.co.saiman.reflection.TypeVariables.typeVariableExtending;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import uk.co.saiman.reflection.AnnotatedTypeSubstitution;
import uk.co.saiman.reflection.AnnotatedTypes;

@SuppressWarnings("javadoc")
public class AnnotatedTypeSubstitutionTest {
	private @interface Test1 {}

	private @interface Test2 {}

	private @interface Test3 {}

	private AnnotatedType createTestType(AnnotatedType type) {
		return parameterize(
				annotated(Map.class),
				wildcardExtending(
						asList(from(Test1.class)),
						arrayFromComponent(arrayFromComponent(annotated(Number.class), from(Test3.class)))),
				parameterize(
						annotated(Map.class),
						wildcardSuper(type),
						annotated(typeVariableExtending(Collection.class, "E"), from(Test2.class))));
	}

	@Test
	public <T extends Number> void noSubstitutionIdentityTest() {
		AnnotatedType type = createTestType(annotated(Number.class, from(Test2.class)));

		AnnotatedType substitution = new AnnotatedTypeSubstitution().where(t -> false, t -> t).resolve(
				type);

		Assert.assertTrue(type == substitution);
	}

	@Test
	public <T extends Number> void doublyNestedWildcardSubstitutionTest() {
		AnnotatedType type = createTestType(annotated(Number.class, from(Test2.class)));

		AnnotatedType expected = createTestType(annotated(Serializable.class));

		AnnotatedType substitution = new AnnotatedTypeSubstitution()
				.where(
						t -> t.getType().equals(Number.class),
						t -> AnnotatedTypes.annotated(Serializable.class))
				.resolve(type);

		Assert.assertEquals(expected.getType(), substitution.getType());
	}
}
