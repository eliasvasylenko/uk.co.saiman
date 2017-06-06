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

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;
import static uk.co.strangeskies.collection.stream.StreamUtilities.upcastStream;
import static uk.co.strangeskies.text.properties.PropertyLoader.getDefaultProperties;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.Workspace;
import uk.co.strangeskies.collection.stream.StreamUtilities;
import uk.co.strangeskies.log.Log;

/**
 * Reference implementation of {@link Workspace}.
 * 
 * @author Elias N Vasylenko
 */
public class WorkspaceImpl implements Workspace {
	private final WorkspaceFactoryImpl factory;
	private final Path dataRoot;

	private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

	private final ExperimentRoot experimentRootType = new ExperimentRootImpl(this);
	private final List<ExperimentImpl> experiments = new ArrayList<>();

	private final ExperimentProperties text;

	private final Lock processingLock = new ReentrantLock();

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param factory
	 *          the factory which produces the workspace
	 * @param workspaceRoot
	 *          the path of the workspace data
	 */
	public WorkspaceImpl(WorkspaceFactoryImpl factory, Path workspaceRoot) {
		this(factory, workspaceRoot, getDefaultProperties(ExperimentProperties.class));
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
	public WorkspaceImpl(
			WorkspaceFactoryImpl factory,
			Path workspaceRoot,
			ExperimentProperties text) {
		this.factory = factory;
		this.dataRoot = workspaceRoot;
		this.text = text;

		loadExperiments();
	}

	private void loadExperiments() {
		PathMatcher filter = dataRoot.getFileSystem().getPathMatcher(
				"glob:**/*" + ExperimentImpl.EXPERIMENT_EXTENSION);

		try (DirectoryStream<Path> stream = newDirectoryStream(
				dataRoot,
				file -> isRegularFile(file) && filter.matches(file))) {
			for (Path path : stream) {
				ExperimentImpl.load(this, path);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	/*
	 * Root experiment types
	 */

	@Override
	public ExperimentRoot getExperimentRootType() {
		return experimentRootType;
	}

	@Override
	public Stream<Experiment> getExperiments() {
		return upcastStream(experiments.stream());
	}

	protected Stream<ExperimentImpl> getExperimentsImpl() {
		return experiments.stream();
	}

	@Override
	public Experiment addExperiment(String name) {
		return addExperiment(name, new PersistedStateImpl());
	}

	protected ExperimentImpl addExperiment(String name, PersistedStateImpl persistedState) {
		if (!ExperimentConfiguration.isNameValid(name)) {
			throw new ExperimentException(text.exception().invalidExperimentName(name));
		}

		ExperimentImpl experiment = new ExperimentImpl(experimentRootType, name, this, persistedState);
		experiments.add(experiment);
		return experiment;
	}

	protected boolean removeExperiment(Experiment experiment) {
		return experiments.remove(experiment);
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
		if (processingLock.tryLock()) {
			try {
				processImpl(node);
			} finally {
				processingLock.unlock();
			}
		} else {
			throw new ExperimentException(
					text.exception().cannotProcessExperimentConcurrently(node.getRoot()));
		}
	}

	private boolean processImpl(ExperimentNodeImpl<?, ?> node) {
		boolean success = StreamUtilities
				.reverse(node.getAncestorsImpl())
				.filter(ExperimentNodeImpl::execute)
				.count() > 0;

		if (success) {
			processChildren(node);
		}

		return success;
	}

	private void processChildren(ExperimentNodeImpl<?, ?> node) {
		node.getChildrenImpl().filter(ExperimentNodeImpl::execute).forEach(this::processChildren);
	}
}
