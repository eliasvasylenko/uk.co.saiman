/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.experiment.api.
 *
 * uk.co.saiman.experiment.api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment;

import static uk.co.strangeskies.reflection.TypeToken.over;

import uk.co.strangeskies.reflection.Reified;
import uk.co.strangeskies.reflection.TypeParameter;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * A type of experiment which may be available to be executed on a particular
 * instrument.
 * 
 * @author Elias N Vasylenko
 *
 * @param <S>
 *          the type of the data describing the experiment state, including
 *          configuration and results
 */
public interface ExperimentType<S> extends Reified {
	/**
	 * @return the human readable name of the experiment type
	 */
	String getName();

	/**
	 * @param forNode
	 *          the node which the configuration is being requested for
	 * @return a new state object suitable for an instance of
	 *         {@link ExperimentNode} over this type.
	 */
	S createState(ExperimentNode<?, ? extends S> forNode);

	/**
	 * Execute this experiment type for a given node. The node may not necessarily
	 * be of this exact type, and may be of a derived type instead.
	 * 
	 * @param node
	 *          the node to be processed
	 */
	void execute(ExperimentNode<?, ? extends S> node);

	/**
	 * Test whether a node of this type may follow from the given directly
	 * preceding node and be validly added as its child.
	 * 
	 * @param parentNode
	 *          the candidate parent node
	 * @return true if a node of this type may be added as a child, false
	 *         otherwise
	 */
	boolean mayComeAfter(ExperimentNode<?, ?> parentNode);

	/**
	 * Test whether a node of the given type may follow from the given node and be
	 * validly added as its child. The penultimate descendant node should be a
	 * descendant of a node of this type.
	 * <p>
	 * This test is performed on all ancestors when an attempt is made to add a
	 * new node.
	 * 
	 * @param penultimateDescendantNode
	 *          the candidate parent node
	 * @param descendantNodeType
	 *          the candidate child node
	 * @return true if a node of the given type may be added as a child of the
	 *         given node, false otherwise
	 */
	boolean mayComeBefore(ExperimentNode<?, ?> penultimateDescendantNode, ExperimentType<?> descendantNodeType);

	/**
	 * @return the exact generic type of the configuration interface
	 */
	default TypeToken<S> getStateType() {
		return getThisType().resolveSupertypeParameters(ExperimentType.class)
				.resolveTypeArgument(new TypeParameter<S>() {});
	}

	@Override
	default TypeToken<?> getThisType() {
		return over(getClass());
	}
}
