/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.utility;

import java.util.Optional;

/**
 * A general interface describing a system with a hierarchical scope for
 * visibility of the contents of that system. Child scopes have visibility over
 * everything visible to their parents, but parents do not have visibility over
 * the contents of their children.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the self bounding of the scoped object
 */
public interface Scoped<T extends Self<T>> extends Self<T> {
	/**
	 * @return the parent scope if one exists, otherwise null
	 */
	Optional<T> getParentScope();

	/**
	 * Collapse this scope into its parent. This will result in the contents of
	 * this scope becoming visible to the parent scope, and all the rest of that
	 * scope's children.
	 * 
	 * @throws NullPointerException
	 *           if the parent scope doesn't exist
	 */
	void collapseIntoParentScope();

	/**
	 * @return a new child scope, with the receiver as its parent
	 */
	T nestChildScope();
}
