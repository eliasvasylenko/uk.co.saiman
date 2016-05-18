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
import static java.util.Collections.unmodifiableSet;
import static uk.co.strangeskies.utilities.text.Localizer.getDefaultLocalizer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentText;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;

/**
 * Reference implementation of {@link ExperimentWorkspace}.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentWorkspaceImpl implements ExperimentWorkspace {
	private final Path dataRoot;
	private final List<ExperimentNode<?>> processingStack = new ArrayList<>();

	private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

	private final ExperimentType<ExperimentConfiguration> rootExperimentType = new RootExperimentType(this);
	private final List<ExperimentNode<ExperimentConfiguration>> rootExperiments = new ArrayList<>();

	private final ExperimentText text;

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param workspaceRoot
	 *          the path of the workspace data
	 */
	public ExperimentWorkspaceImpl(Path workspaceRoot) {
		this(workspaceRoot, getDefaultLocalizer().getLocalization(ExperimentText.class));
	}

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param workspaceRoot
	 *          the path of the workspace data
	 * @param text
	 *          a localised text accessor implementation
	 */
	public ExperimentWorkspaceImpl(Path workspaceRoot, ExperimentText text) {
		this.dataRoot = workspaceRoot;
		this.text = text;
	}

	ExperimentText getText() {
		return text;
	}

	@Override
	public Path getWorkspaceDataRoot() {
		return dataRoot;
	}

	@Override
	public List<ExperimentNode<?>> processingState() {
		return processingStack;
	}

	/*
	 * Root experiment types
	 */

	@Override
	public ExperimentType<ExperimentConfiguration> getRootExperimentType() {
		return rootExperimentType;
	}

	@Override
	public List<ExperimentNode<ExperimentConfiguration>> getRootExperiments() {
		return unmodifiableList(rootExperiments);
	}

	@Override
	public ExperimentNode<ExperimentConfiguration> addRootExperiment(String name) {
		ExperimentNode<ExperimentConfiguration> rootExperiment = new ExperimentNodeImpl<>(rootExperimentType, this);
		rootExperiment.getState().setName(name);

		rootExperiments.add(rootExperiment);

		return rootExperiment;
	}

	void removeRootExperiment(ExperimentNode<ExperimentConfiguration> rootNode) {
		rootExperiments.remove(rootNode);
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
		return unmodifiableSet(experimentTypes);
	}
}
