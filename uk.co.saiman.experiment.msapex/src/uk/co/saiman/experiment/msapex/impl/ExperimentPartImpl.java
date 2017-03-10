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
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.impl;

import static uk.co.strangeskies.fx.FxmlLoadBuilder.buildWith;
import static uk.co.strangeskies.reflection.token.TypedObject.typedObject;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IAdapterManager;
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
import uk.co.saiman.experiment.msapex.ExperimentPart;
import uk.co.strangeskies.eclipse.EclipseModularTreeController;

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
@RequireCapability(ns = ExperimentPartImpl.OSGI_SERVICE, filter = "(" + Constants.OBJECTCLASS
		+ "=uk.co.saiman.experiment.ExperimentWorkspaceFactory)")
public class ExperimentPartImpl implements ExperimentPart {
	static final String OSGI_SERVICE = "osgi.service";

	@FXML
	private EclipseModularTreeController modularTreeController;
	private ExperimentWorkspace workspace;

	@Inject
	private IAdapterManager adapterManager;
	private ExperimentNodeAdapterFactory adapterFactory;

	@PostConstruct
	void initialize(
			BorderPane container,
			@LocalInstance FXMLLoader loader,
			@Named(E4Workbench.INSTANCE_LOCATION) Location instanceLocation,
			ExperimentWorkspaceFactory workspaceFactory) {
		container.setCenter(buildWith(loader).controller(ExperimentPart.class, this).loadRoot());

		Path workspaceLocation;
		try {
			workspaceLocation = Paths.get(instanceLocation.getURL().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		workspace = workspaceFactory.openWorkspace(workspaceLocation);

		modularTreeController.getTreeView().setRootData(typedObject(ExperimentWorkspace.class, workspace));

		adapterFactory = new ExperimentNodeAdapterFactory(adapterManager, workspace);
	}

	@PreDestroy
	void destroy() {
		adapterFactory.unregister();
	}

	@Override
	public ExperimentWorkspace getExperimentWorkspace() {
		return workspace;
	}

	@Override
	public EclipseModularTreeController getExperimentTreeController() {
		return modularTreeController;
	}
}
