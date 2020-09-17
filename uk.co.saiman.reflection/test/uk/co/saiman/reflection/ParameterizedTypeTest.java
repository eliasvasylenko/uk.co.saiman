/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static uk.co.saiman.reflection.ParameterizedTypes.parameterize;

import java.lang.reflect.ParameterizedType;

import org.junit.jupiter.api.Test;

import uk.co.saiman.reflection.ParameterizedTypeTest.Outer.Inner;

@SuppressWarnings("javadoc")
public class ParameterizedTypeTest {
  static class Outer<T> {
    class Inner<U> {}
  }

  @Test
  public void parameterizedEnclosingTypeTest() {
    ParameterizedType parameterizedType = parameterize(Inner.class, String.class, Number.class);

    assertThat(parameterizedType.getOwnerType(), instanceOf(ParameterizedType.class));

    assertThat(
        ((ParameterizedType) parameterizedType.getOwnerType()).getActualTypeArguments(),
        arrayContaining(String.class));
  }
}
