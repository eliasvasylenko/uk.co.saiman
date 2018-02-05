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

import static java.util.stream.Collectors.toList;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInRelativeOrder.containsInRelativeOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static uk.co.saiman.reflection.ParameterizedTypes.parameterize;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import uk.co.saiman.reflection.TypeHierarchy;

@SuppressWarnings("javadoc")
public class SuperTypeParametersTest {
	static class Outer<T> {
		class Inner<U> {}
	}

	@Test
	public void indirectParameterizedSupertype() {
		ParameterizedType parameterizedType = parameterize(HashSet.class, String.class);

		Type supertype = new TypeHierarchy(parameterizedType).resolveSupertype(Iterable.class);

		assertThat(supertype, equalTo(parameterize(Iterable.class, String.class)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void partialOrderingOverAllSupertypes() {
		List<Class<?>> supertypes = new TypeHierarchy(A_SUPER.class)
				.resolveCompleteSupertypeHierarchy(Object.class)
				.map(t -> ((Class<?>) t))
				.collect(toList());

		assertThat(
				supertypes,
				containsInRelativeOrder(
						A_SUPER.class,
						B_SUPER.class,
						C_SUPER.class,
						E_SUPER.class,
						G_SUPER.class,
						H_SUPER.class));

		assertThat(
				supertypes,
				containsInRelativeOrder(
						A_SUPER.class,
						B_SUPER.class,
						D_SUPER.class,
						F_SUPER.class,
						G_SUPER.class,
						H_SUPER.class));

		assertThat(
				supertypes,
				containsInAnyOrder(
						A_SUPER.class,
						B_SUPER.class,
						C_SUPER.class,
						D_SUPER.class,
						E_SUPER.class,
						F_SUPER.class,
						G_SUPER.class,
						H_SUPER.class,
						Object.class));
	}

	@Test
	public void directGenericSupertypeOfRawSupertypes() {
		Type supertype = new TypeHierarchy(A_ERASURE.class).resolveSupertype(C_ERASURE.class);

		assertThat(supertype, equalTo(C_ERASURE.class));
	}

	@Test
	public void indirectGenericSupertypeOfRawSupertypes() {
		Type supertype = new TypeHierarchy(A_ERASURE.class).resolveSupertype(E_ERASURE.class);

		assertThat(supertype, equalTo(parameterize(E_ERASURE.class, String.class)));
	}
}

interface A_ERASURE<T> extends C_ERASURE<String>, D_ERASURE {}

interface C_ERASURE<T> {}

interface D_ERASURE extends E_ERASURE<String> {}

interface E_ERASURE<T> {}

interface A_SUPER extends B_SUPER {}

interface B_SUPER extends C_SUPER, D_SUPER {}

interface C_SUPER extends E_SUPER {}

interface D_SUPER extends F_SUPER {}

interface E_SUPER extends G_SUPER {}

interface F_SUPER extends G_SUPER {}

interface G_SUPER extends H_SUPER {}

interface H_SUPER {}
