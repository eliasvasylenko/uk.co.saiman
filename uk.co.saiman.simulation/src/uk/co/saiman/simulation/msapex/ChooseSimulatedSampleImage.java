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
package uk.co.saiman.simulation.msapex;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.msapex.experiment.ExperimentPart;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.reflection.jar.JarUtilities;

/**
 * Add an experiment to the workspace
 * 
 * @author Elias N Vasylenko
 */
public class ChooseSimulatedSampleImage {
	/**
	 * The ID of the command in the e4 model fragment.
	 */
	public static final String COMMAND_ID = "uk.co.saiman.simulation.msapex.command.choosesimulatedsampleimage";

	@Execute
	void execute(MPart part, @Localize ExperimentProperties text) throws IOException {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();
		TreeItemData<?> itemData = experimentPart.getExperimentTreeController().getSelectionData();

		if (!(itemData.data() instanceof SimulatedSampleImage)) {
			throw new ExperimentException(text.exception().illegalCommandForSelection(COMMAND_ID, itemData.data()));
		}

		TreeItemData<?> parentData = itemData.parent().orElseThrow(
				() -> new ExperimentException(text.exception().illegalCommandForSelection(COMMAND_ID, itemData.data())));

		if (!(parentData.data() instanceof SimulatedSampleImageConfiguration)) {
			throw new ExperimentException(text.exception().illegalCommandForSelection(COMMAND_ID, itemData.data()));
		}

		SimulatedSampleImageConfiguration selectedConfiguration = (SimulatedSampleImageConfiguration) parentData.data();

		FileSystem fileSystem = JarUtilities.getContainingJarFileSystem(getClass());

		Path imagePath = fileSystem.getPath(getClass().getPackage().getName().replace('.', '/'));
		for (Path imageFile : imagePath) {
			if (!Files.isDirectory(imageFile) && imageFile.endsWith(".png")) {
				System.out.println(imageFile);
			}
		}
	}
}
