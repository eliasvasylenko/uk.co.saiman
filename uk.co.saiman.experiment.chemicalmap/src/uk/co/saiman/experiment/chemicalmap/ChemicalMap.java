/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.experiment.chemicalmap.
 *
 * uk.co.saiman.experiment.chemicalmap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.chemicalmap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.chemicalmap;

import uk.co.saiman.data.spectrum.Spectrum;

public interface ChemicalMap {
  /**
   * @return the number of horizontally indexable coordinates
   */
  int getWidth();

  /**
   * @return the number of vertically indexable coordinates
   */
  int getHeight();

  /**
   * @return the number of indexable masses
   */
  int getDepth();

  /**
   * @return the sum of the spectra at each considered coordinate in the map
   */
  Spectrum getSpectrum();

  /**
   * Lazily load the image of the intensities summed over all considered mass
   * indices.
   * 
   * @return the image for the considered mass indices
   */
  Image getImage();

  /**
   * Derive a view of the chemical map which only considers intensities at the
   * given mass indices to produce a summed image.
   * 
   * @param massIndices
   *          the indices of each mass intensity sample we wish to consider
   * @return the derived view
   */
  Image getImage(MassIndices massIndices);

  /**
   * Derive a view of the chemical map which only considers the spectra at the
   * given coordinates to produce a summed spectrum.
   * 
   * @param imageCoordinates
   *          the coordinates of each spectrum we wish to consider
   * @return the derived view
   */
  Spectrum getSpectrum(ImageCoordinates imageCoordinates);

  /**
   * As @see #forMassIndices(MassIndices) for all coordinates within the image.
   */
  @SuppressWarnings("javadoc")
  ChemicalMap forAllOfImage();

  void save();
}
