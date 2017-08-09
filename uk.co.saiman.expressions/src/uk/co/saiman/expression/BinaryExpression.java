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

import java.util.function.BiFunction;

import uk.co.saiman.expression.Expression;
import uk.co.saiman.expression.PassiveExpression;
import uk.co.saiman.expression.UnaryExpression;

/**
 * As {@link UnaryExpression}, but with two operands.
 *
 * @author Elias N Vasylenko
 * @param <O1>
 *          The type of the first operand.
 * @param <O2>
 *          The type of the second operand.
 * @param <R>
 *          The type of the result.
 */
public abstract class BinaryExpression<O1, O2, R> extends PassiveExpression<R> {
  private final Expression<? extends O1> firstOperand;
  private final Expression<? extends O2> secondOperand;
  private final BiFunction<? super O1, ? super O2, ? extends R> operation;

  /**
   * @param firstOperand
   *          An expression providing the first operand for the function.
   * @param secondOperand
   *          An expression providing the second operand for the function.
   * @param operation
   *          A function transforming the operands into a value of this
   *          expression's type.
   */
  public BinaryExpression(
      Expression<? extends O1> firstOperand,
      Expression<? extends O2> secondOperand,
      BiFunction<? super O1, ? super O2, ? extends R> operation) {
    super(firstOperand, secondOperand);

    this.firstOperand = firstOperand;
    this.secondOperand = secondOperand;
    this.operation = operation;
  }

  /**
   * @return The first operand expression.
   */
  public Expression<? extends O1> getFirstOperand() {
    return firstOperand;
  }

  /**
   * @return The second operand expression.
   */
  public Expression<? extends O2> getSecondOperand() {
    return secondOperand;
  }

  @Override
  protected R evaluate() {
    return operation.apply(firstOperand.getValue(), secondOperand.getValue());
  }
}
