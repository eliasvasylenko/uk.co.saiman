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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.fx.core.di.LocalInstance;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Reference;

import aQute.bnd.annotation.headers.RequireCapability;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.ExperimentManager;
import uk.co.strangeskies.fx.FXUtilities;

/**
 * Experiment management view part. Manage experiments and their results in the
 * experiment tree.
 * 
 * @author Elias N Vasylenko
 */
@RequireCapability(ns = ExperimentPart.OSGI_SERVICE, filter = "(" + Constants.OBJECTCLASS
		+ "=uk.co.saiman.experiment.ExperimentManager)")
public class ExperimentPart {
	static final String OSGI_SERVICE = "osgi.service";

	@Inject
	@Reference
	ExperimentManager manager;

	@PostConstruct
	void initialise(BorderPane container, @LocalInstance FXMLLoader loader) {
		container.setCenter(FXUtilities.loadIntoController(loader, this));
	}
}
