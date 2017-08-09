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
 * This file is part of uk.co.saiman.expressions.
 *
 * uk.co.saiman.expressions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.expressions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.expression;

import uk.co.saiman.expression.Expression;
import uk.co.saiman.expression.SelfExpression;
import uk.co.saiman.utility.Self;

/**
 * A variable for use in reactive programming. A Variable in this sense is a
 * first class expression, that is to say it is an expression whose value is
 * itself.
 *
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          See {@link Self} for more information. This must be self-bounding as
 *          the value of the expression is the variable itself.
 */
public interface SelfExpression<S extends SelfExpression<S>> extends Expression<S>, Self<S> {}
