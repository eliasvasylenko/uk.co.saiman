/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import static uk.co.strangeskies.fx.FXMLLoadBuilder.buildWith;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Constants;

import aQute.bnd.annotation.headers.RequireCapability;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.ExperimentWorkspaceFactory;
import uk.co.strangeskies.eclipse.ModularTreeController;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * Experiment management view part. Manage experiments and their results in the
 * experiment tree.
 * 
 * @author Elias N Vasylenko
 */
/*
 * Specify a service capability requirement on the ExperimentWorkspaceFactory
 * injection via the bundle manifest.
 */
@RequireCapability(ns = ExperimentPart.OSGI_SERVICE, filter = "(" + Constants.OBJECTCLASS
		+ "=uk.co.saiman.experiment.ExperimentWorkspaceFactory)")
public class ExperimentPart {
	static final String OSGI_SERVICE = "osgi.service";

	@FXML
	private ModularTreeController modularTreeController;
	private ExperimentWorkspace workspace;

	@PostConstruct
	void initialise(BorderPane container, @LocalInstance FXMLLoader loader,
			@Named(E4Workbench.INSTANCE_LOCATION) Location instanceLocation, ExperimentWorkspaceFactory workspaceFactory) {
		container.setCenter(buildWith(loader).controller(this).loadRoot());

		Path workspaceLocation;
		try {
			workspaceLocation = Paths.get(instanceLocation.getURL().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		workspace = workspaceFactory.openWorkspace(workspaceLocation);

		modularTreeController.getTreeView().setRootData(new TypeToken<ExperimentWorkspace>() {}.typedObject(workspace));
	}

	/**
	 * @return the current experiment workspace
	 */
	public ExperimentWorkspace getExperimentWorkspace() {
		return workspace;
	}

	/**
	 * @return the controller for the experiment tree UI item
	 */
	public ModularTreeController getExperimentTreeController() {
		return modularTreeController;
	}
}
