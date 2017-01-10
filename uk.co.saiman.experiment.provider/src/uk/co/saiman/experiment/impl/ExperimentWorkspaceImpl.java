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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static uk.co.strangeskies.utilities.collection.StreamUtilities.upcastStream;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.text.properties.PropertyLoader;
import uk.co.strangeskies.utilities.Log;
import uk.co.strangeskies.utilities.collection.StreamUtilities;

/**
 * Reference implementation of {@link ExperimentWorkspace}.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentWorkspaceImpl implements ExperimentWorkspace {
	private final ExperimentWorkspaceFactoryImpl factory;
	private final Path dataRoot;

	private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

	private final RootExperiment rootExperimentType = new RootExperimentImpl(this);
	private final List<ExperimentNodeImpl<RootExperiment, ExperimentConfiguration>> rootExperiments = new ArrayList<>();

	private final ExperimentProperties text;

	private final List<ExperimentNodeImpl<?, ?>> processingStack = new ArrayList<>();
	private final Lock processingLock = new ReentrantLock();

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param factory
	 *          the factory which produces the workspace
	 * @param workspaceRoot
	 *          the path of the workspace data
	 */
	public ExperimentWorkspaceImpl(ExperimentWorkspaceFactoryImpl factory, Path workspaceRoot) {
		this(factory, workspaceRoot, PropertyLoader.getDefaultPropertyLoader().getProperties(ExperimentProperties.class));
	}

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param factory
	 *          the factory which produces the workspace
	 * @param workspaceRoot
	 *          the path of the workspace data
	 * @param text
	 *          a localized text accessor implementation
	 */
	public ExperimentWorkspaceImpl(
			ExperimentWorkspaceFactoryImpl factory,
			Path workspaceRoot,
			ExperimentProperties text) {
		this.factory = factory;
		this.dataRoot = workspaceRoot;
		this.text = text;
	}

	Log getLog() {
		return factory.getLog();
	}

	ExperimentProperties getText() {
		return text;
	}

	@Override
	public Path getWorkspaceDataPath() {
		return dataRoot;
	}

	@Override
	public Stream<ExperimentNode<?, ?>> processingState() {
		return upcastStream(processingStack.stream());
	}

	/*
	 * Root experiment types
	 */

	@Override
	public RootExperiment getRootExperimentType() {
		return rootExperimentType;
	}

	@Override
	public Stream<ExperimentNode<RootExperiment, ExperimentConfiguration>> getRootExperiments() {
		return upcastStream(rootExperiments.stream());
	}

	protected Stream<ExperimentNodeImpl<RootExperiment, ExperimentConfiguration>> getRootExperimentsImpl() {
		return rootExperiments.stream();
	}

	@Override
	public ExperimentNode<RootExperiment, ExperimentConfiguration> addRootExperiment(String name) {
		if (!ExperimentConfiguration.isNameValid(name)) {
			throw new ExperimentException(text.exception().invalidExperimentName(name));
		}

		ExperimentNodeImpl<RootExperiment, ExperimentConfiguration> rootExperiment = new ExperimentNodeImpl<>(
				rootExperimentType,
				this);
		rootExperiment.getState().setName(name);

		rootExperiments.add(rootExperiment);

		return rootExperiment;
	}

	protected boolean removeRootExperiment(ExperimentNode<?, ExperimentConfiguration> rootNode) {
		return rootExperiments.remove(rootNode);
	}

	/*
	 * Child experiment types
	 */

	@Override
	public boolean registerExperimentType(ExperimentType<?> experimentType) {
		return experimentTypes.add(experimentType);
	}

	@Override
	public boolean unregisterExperimentType(ExperimentType<?> experimentType) {
		return experimentTypes.remove(experimentType);
	}

	@Override
	public Stream<ExperimentType<?>> getRegisteredExperimentTypes() {
		return Stream.concat(factory.getRegisteredExperimentTypes(), experimentTypes.stream());
	}

	protected void process(ExperimentNodeImpl<?, ?> node) {
		if (!tryProcess(node)) {
			throw new ExperimentException(text.exception().cannotProcessExperimentConcurrently(node.getRoot()));
		}
	}

	protected boolean tryProcess(ExperimentNodeImpl<?, ?> node) {
		if (processingLock.tryLock()) {
			try {
				tryProcessImpl(node);
			} finally {
				processingLock.unlock();
			}
		}

		return true;
	}

	private void tryProcessImpl(ExperimentNodeImpl<?, ?> node) {
		boolean success = StreamUtilities.reverse(node.getAncestorsImpl()).filter(ExperimentNodeImpl::execute).count() > 0;

		if (success) {
			processChildren(node);
		}
	}

	private void processChildren(ExperimentNodeImpl<?, ?> node) {
		node.getChildrenImpl().filter(ExperimentNodeImpl::execute).forEach(this::processChildren);
	}
}
