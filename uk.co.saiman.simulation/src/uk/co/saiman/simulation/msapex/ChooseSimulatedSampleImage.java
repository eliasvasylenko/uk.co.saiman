/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.msapex.ExperimentPart;
import uk.co.saiman.simulation.SimulationProperties;
import uk.co.saiman.simulation.experiment.SimulatedSampleImageConfiguration;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;
import uk.co.strangeskies.eclipse.Localize;
import uk.co.strangeskies.fx.TreeItemData;
import uk.co.strangeskies.reflection.resource.Jar;

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

	@Inject
	@Localize
	SimulationProperties text;

	private Path tempDirectory;

	private static final String SAMPLE_IMAGES_DIRECTORY = "Sample-Images";
	private static final String PNG = ".png";

	@Execute
	synchronized void execute(MPart part) throws IOException {
		ExperimentPart experimentPart = (ExperimentPart) part.getObject();
		TreeItemData<?> itemData = experimentPart.getExperimentTreeController().getSelectionData();

		if (!(itemData.data() instanceof SimulatedSampleImage)) {
			throw new ExperimentException(
					text.experiment().exception().illegalCommandForSelection(COMMAND_ID, itemData.data()));
		}

		TreeItemData<?> parentData = itemData.parent().orElseThrow(
				() -> new ExperimentException(
						text.experiment().exception().illegalCommandForSelection(COMMAND_ID, itemData.data())));

		if (!(parentData.data() instanceof SimulatedSampleImageConfiguration)) {
			throw new ExperimentException(
					text.experiment().exception().illegalCommandForSelection(COMMAND_ID, itemData.data()));
		}

		SimulatedSampleImageConfiguration selectedConfiguration = (SimulatedSampleImageConfiguration) parentData.data();

		Path imageDirectory = getImageDirectory();

		Path imagePath = chooseImageFile(
				imageDirectory,
				experimentPart.getExperimentTreeController().getTreeView().getScene().getWindow());

		if (imagePath != null) {
			selectedConfiguration.setSampleImage(loadSimulatedSampleImage(imagePath));

			experimentPart.getExperimentTreeController().getTreeView().refresh();
		}
	}

	private SimulatedSampleImage loadSimulatedSampleImage(Path imagePath) throws IOException {
		Image image = new Image(Files.newInputStream(imagePath));

		return new SimulatedSampleImage() {
			@Override
			public int getWidth() {
				return (int) image.getWidth();
			}

			@Override
			public int getHeight() {
				return (int) image.getHeight();
			}

			@Override
			public double getRed(int x, int y) {
				return image.getPixelReader().getColor(x, y).getRed();
			}

			@Override
			public double getGreen(int x, int y) {
				return image.getPixelReader().getColor(x, y).getGreen();
			}

			@Override
			public double getBlue(int x, int y) {
				return image.getPixelReader().getColor(x, y).getBlue();
			}

			@Override
			public String toString() {
				return imagePath.getFileName().toString();
			}
		};
	}

	private Path chooseImageFile(Path imageDirectory, Window scene) throws IOException {
		final FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle(text.loadSampleImageTitle().toString());

		fileChooser.setInitialDirectory(imageDirectory.toFile());

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				text.imageFileFilterTitle().toString(),
				text.imageFileFilter());
		fileChooser.getExtensionFilters().add(extensionFilter);

		File file = fileChooser.showOpenDialog(scene);

		return file == null ? null : file.toPath();
	}

	private Path getImageDirectory() throws IOException {
		if (tempDirectory == null) {
			tempDirectory = Files.createTempDirectory(SAMPLE_IMAGES_DIRECTORY);

			Path imagePath = Jar.getContainingJar(getClass()).getPackagePath(getClass().getPackage());

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagePath)) {
				for (Path imageFile : stream) {
					String fileName = imageFile.getFileName().toString();

					if (!Files.isDirectory(imageFile) && fileName.endsWith(PNG)) {
						Files.copy(imageFile, tempDirectory.resolve(fileName));
					}
				}
			}
		}

		return tempDirectory;
	}
}
