/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Collections.unmodifiableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.RootExperiment;
import uk.co.strangeskies.text.properties.PropertyLoader;

/**
 * Reference implementation of {@link ExperimentWorkspace}.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentWorkspaceImpl implements ExperimentWorkspace {
	private final ExperimentWorkspaceFactoryImpl factory;
	private final Path dataRoot;
	private final List<ExperimentNode<?, ?>> processingStack = new ArrayList<>();

	private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

	private final RootExperiment rootExperimentType = new RootExperimentImpl(this);
	private final List<ExperimentNode<RootExperiment, ExperimentConfiguration>> rootExperiments = new ArrayList<>();

	private final ExperimentProperties text;

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
	public ExperimentWorkspaceImpl(ExperimentWorkspaceFactoryImpl factory, Path workspaceRoot, ExperimentProperties text) {
		this.factory = factory;
		this.dataRoot = workspaceRoot;
		this.text = text;
	}

	ExperimentProperties getText() {
		return text;
	}

	@Override
	public Path getWorkspaceDataRoot() {
		return dataRoot;
	}

	@Override
	public List<ExperimentNode<?, ?>> processingState() {
		return processingStack;
	}

	/*
	 * Root experiment types
	 */

	@Override
	public RootExperiment getRootExperimentType() {
		return rootExperimentType;
	}

	@Override
	public List<ExperimentNode<RootExperiment, ExperimentConfiguration>> getRootExperiments() {
		return unmodifiableList(rootExperiments);
	}

	@Override
	public ExperimentNode<RootExperiment, ExperimentConfiguration> addRootExperiment(String name) {
		ExperimentNode<RootExperiment, ExperimentConfiguration> rootExperiment = new ExperimentNodeImpl<>(
				rootExperimentType, this);
		rootExperiment.getState().setName(name);

		rootExperiments.add(rootExperiment);

		return rootExperiment;
	}

	boolean removeRootExperiment(ExperimentNode<?, ExperimentConfiguration> rootNode) {
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
	public Set<ExperimentType<?>> getRegisteredExperimentTypes() {
		Set<ExperimentType<?>> experimentTypes = new HashSet<>(factory.getRegisteredExperimentTypes());
		experimentTypes.addAll(this.experimentTypes);
		return experimentTypes;
	}
}
