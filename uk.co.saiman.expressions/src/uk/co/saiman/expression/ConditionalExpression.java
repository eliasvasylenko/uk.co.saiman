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
import uk.co.saiman.expression.TrinaryExpression;

/**
 * An {@link Expression} whose primary dependency is conditional on a
 * {@link Boolean} {@link Expression} dependency. The primary dependency, in
 * this instance, provides the value of this {@link Expression} directly.
 * 
 * @author Elias N Vasylenko
 * @param <O>
 *          The type of the expression.
 */
public class ConditionalExpression<O> extends TrinaryExpression<Boolean, O, O, O> {
  /**
   * @param condition
   *          The condition to switch between primary dependencies.
   * @param expressionWhenFulfilled
   *          The {@link Expression} to set as primary dependency when the given
   *          condition is fulfilled.
   * @param expressionWhenUnfulfilled
   *          The {@link Expression} to set as primary dependency when the given
   *          condition is unfulfilled.
   */
  public ConditionalExpression(
      Expression<? extends Boolean> condition,
      Expression<? extends O> expressionWhenFulfilled,
      Expression<? extends O> expressionWhenUnfulfilled) {
    super(condition, expressionWhenFulfilled, expressionWhenUnfulfilled, (c, f, u) -> c ? f : u);
  }

  /**
   * @return The condition to switch between primary dependencies.
   */
  public final Expression<? extends Boolean> getCondition() {
    return getFirstOperand();
  }

  /**
   * @return The {@link Expression} which behaves as primary dependency when the
   *         given condition is fulfilled.
   */
  public final Expression<? extends O> getExpressionWhenFulfilled() {
    return getSecondOperand();
  }

  /**
   * @return The {@link Expression} which behaves as primary dependency when the
   *         given condition is unfulfilled.
   */
  public final Expression<? extends O> getExpressionWhenUnfulfilled() {
    return getThirdOperand();
  }
}
