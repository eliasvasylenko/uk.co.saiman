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
package uk.co.saiman.reflection.matchers;

import java.lang.reflect.Type;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import uk.co.saiman.reflection.Types;

public class IsSubtypeOf extends BaseMatcher<Type> {
  private final Type assignmentTarget;

  private IsSubtypeOf(Type assignmentTarget) {
    this.assignmentTarget = assignmentTarget;
  }

  public static Matcher<Type> isSubtypeOf(Type target) {
    return new IsSubtypeOf(target);
  }

  @Override
  public boolean matches(Object item) {
    return (item == null || item instanceof Type) && Types.isSubtype((Type) item, assignmentTarget);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(" subtype of " + assignmentTarget);
  }
}
