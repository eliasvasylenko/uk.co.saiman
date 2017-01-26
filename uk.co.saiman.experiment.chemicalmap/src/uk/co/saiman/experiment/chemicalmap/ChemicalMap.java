package uk.co.saiman.experiment.chemicalmap;

import uk.co.saiman.experiment.spectrum.Spectrum;

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
	 * given mass indices when {@link #getImage() producing a summed image}.
	 * 
	 * @param massIndices
	 *          the indices of each mass intensity sample we wish to consider
	 * @return the derived view
	 */
	ChemicalMap forMassIndices(MassIndices massIndices);

	/**
	 * As @see #forMassIndices(MassIndices) for all masses.
	 */
	@SuppressWarnings("javadoc")
	ChemicalMap forAllMasses();

	/**
	 * Derive a view of the chemical map which only considers the spectra at the
	 * given coordinates when {@link #getSpectrum() producing a summed spectrum}.
	 * 
	 * @param imageCoordinates
	 *          the coordinates of each spectrum we wish to consider
	 * @return the derived view
	 */
	ChemicalMap forImageCoordinates(ImageCoordinates imageCoordinates);

	/**
	 * As @see #forMassIndices(MassIndices) for all coordinates within the image.
	 */
	@SuppressWarnings("javadoc")
	ChemicalMap forAllOfImage();

	void save();
}
