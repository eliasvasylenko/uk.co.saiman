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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley;

public class CopleyCommand {
	private static final String SPLIT_CHARACTERS = "::";

	private final CopleyOperation operation;

	private CopleyCommand(CopleyOperation operation) {
		this.operation = operation;
	}

	public static CopleyCommand copleyCommand(CopleyOperation operation) {
		return new CopleyCommand(operation);
	}

	public static CopleyVariableCommand copleyCommand(
			CopleyOperation operation,
			CopleyVariable variable) {
		return new CopleyVariableCommand(operation, variable);
	}

	public CopleyOperation getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return operation.toString();
	}

	public static class CopleyVariableCommand extends CopleyCommand {
		private final CopleyVariable variable;

		public CopleyVariableCommand(CopleyOperation operation, CopleyVariable variable) {
			super(operation);
			this.variable = variable;
		}

		public CopleyVariable getVariable() {
			return variable;
		}

		@Override
		public String toString() {
			return variable + SPLIT_CHARACTERS + super.toString();
		}
	}
}
