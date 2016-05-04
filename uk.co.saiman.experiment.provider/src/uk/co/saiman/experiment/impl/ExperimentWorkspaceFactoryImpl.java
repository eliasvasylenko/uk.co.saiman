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

import java.nio.file.Path;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.ExperimentWorkspaceFactory;

/**
 * Reference implementation of {@link ExperimentWorkspaceFactory}.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class ExperimentWorkspaceFactoryImpl implements ExperimentWorkspaceFactory {
	@Override
	public ExperimentWorkspace openWorkspace(Path location) {
		return new ExperimentWorkspaceImpl(location);
	}
}
