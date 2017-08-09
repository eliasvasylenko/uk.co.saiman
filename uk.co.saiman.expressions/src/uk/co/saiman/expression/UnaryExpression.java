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

import java.util.function.Function;

import uk.co.saiman.expression.Expression;
import uk.co.saiman.expression.PassiveExpression;

/**
 * An implementation of {@link Expression} with a single data dependency, whose
 * value is derived through the application of a function to the value of that
 * dependency. This function may also be provided through an {@link Expression}
 * dependency.
 * 
 * @author Elias N Vasylenko
 * @param <O>
 *          The type of the operand.
 * @param <R>
 *          The type of the result.
 */
public abstract class UnaryExpression<O, R> extends PassiveExpression<R> {
  private final Expression<? extends O> operand;
  private final Function<? super O, ? extends R> operation;

  /**
   * @param operand
   *          An expression providing an operand for the function.
   * @param operation
   *          A function transforming an operand into a value of this expression's
   *          type.
   */
  public UnaryExpression(
      Expression<? extends O> operand,
      Function<? super O, ? extends R> operation) {
    super(operand);

    this.operand = operand;
    this.operation = operation;
  }

  /**
   * @return The operand expression.
   */
  public Expression<? extends O> getOperand() {
    return operand;
  }

  @Override
  protected R evaluate() {
    return operation.apply(operand.getValue());
  }
}
