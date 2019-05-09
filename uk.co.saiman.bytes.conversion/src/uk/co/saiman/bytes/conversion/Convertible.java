/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.bytes.conversion.
 *
 * uk.co.saiman.bytes.conversion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.bytes.conversion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.bytes.conversion;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A meta-annotation to indicate a byte conversion annotation.
 * <p>
 * A byte conversion annotation is an annotation which carries information
 * intended to guide conversion between a type and a sequence of bits. Typically
 * the {@link Target target} of a byte conversion annotation should be
 * {@link ElementType#TYPE type} and {@link ElementType#TYPE_USE type use}, so
 * as to indicate that the annotated type should be handled by a converter which
 * {@link ByteConverterProvider#supportsAnnotation(Class) supports} the
 * annotation.
 * <p>
 * A converter provider is only applied to a target if it advertises
 * {@link ByteConverterProvider#supportsAnnotation(Class) support} for all the
 * {@link Convertible} annotations on the target.
 * 
 * @author Elias N Vasylenko
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface Convertible {}
