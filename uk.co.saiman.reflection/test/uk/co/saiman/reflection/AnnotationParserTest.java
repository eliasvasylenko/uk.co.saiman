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

import uk.co.saiman.reflection.Annotations;
import uk.co.saiman.reflection.Annotations.AnnotationParser;

/**
 * Tests for {@link AnnotationParser} class. This is necessary alongside
 * {@link AnnotationsTest}, for two reasons.
 * <p>
 * Firstly, though those tests do cover parsing, they only do so of annotation
 * strings formatted exactly according to the expected output of
 * {@link Annotations#toString(java.lang.annotation.Annotation)}. Secondly, they
 * do not cover partial
 * <p>
 * These test cases aim to cover formatting which is valid parsing input, but
 * differs from expected string output.
 * 
 * @author Elias N Vasylenko
 */
// @RunWith(Theories.class)
public class AnnotationParserTest {}
