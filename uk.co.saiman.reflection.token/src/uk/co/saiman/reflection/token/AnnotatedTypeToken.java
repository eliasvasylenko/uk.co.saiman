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
 * This file is part of uk.co.saiman.reflection.token.
 *
 * uk.co.saiman.reflection.token is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.reflection.token is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection.token;

import java.lang.reflect.AnnotatedType;

public class AnnotatedTypeToken<T> extends TypeToken<T> {
  private final AnnotatedType annotatedType;

  protected AnnotatedTypeToken() {
    super(AnnotatedTypeToken.class);
    this.annotatedType = resolveSuperclassParameter(AnnotatedTypeToken.class);
  }

  protected AnnotatedTypeToken(AnnotatedType annotatedType) {
    super(annotatedType.getType());
    this.annotatedType = annotatedType;
  }

  public AnnotatedType getAnnotatedType() {
    return annotatedType;
  }

  public static AnnotatedTypeToken<?> forType(AnnotatedType type) {
    return new AnnotatedTypeToken<>(type);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AnnotatedTypeToken<?>
        && getAnnotatedType().equals(((AnnotatedTypeToken<?>) obj).getAnnotatedType());
  }

  @Override
  public int hashCode() {
    return getAnnotatedType().hashCode();
  }
}
