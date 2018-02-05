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
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview;

/**
 * This is a marker interface for OSGi services to signify an injectable
 * {@link ModularTreeView} contribution. Services with higher service rankings
 * are applied later so that they can choose to override other contributions.
 * <p>
 * Implementations which have fields injected should be registered as prototype
 * scope services, as if an instance is shared each tree will re-inject from
 * their own context.
 * <P>
 * TODO If this system is eventually migrated to an e4 model based definition,
 * this will probably be deprecated. It's function is only to allow OSGi-DS to
 * wire up the contributions to trees in the meantime. In such a hypothetical
 * system, ordering between contributions would probably be achieved by some
 * sort of "before:id" "after:id" type system as used by many other types of
 * model element, instead of service ranking.
 * 
 * @author Elias N Vasylenko
 */
public interface TreeContribution {}
