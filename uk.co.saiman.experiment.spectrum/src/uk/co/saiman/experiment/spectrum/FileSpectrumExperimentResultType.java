package uk.co.saiman.experiment.spectrum;

import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.strangeskies.reflection.token.TypeToken;

public class FileSpectrumExperimentResultType<T> implements ExperimentResultType<DefaultFileSpectrum> {
	private final SpectrumExperimentType<?> type;

	FileSpectrumExperimentResultType(SpectrumExperimentType<?> type) {
		this.type = type;
	}

	@Override
	public String getName() {
		return type.getProperties().spectrumResultName().toString();
	}

	@Override
	public TypeToken<DefaultFileSpectrum> getDataType() {
		return new TypeToken<DefaultFileSpectrum>() {};
	}
}
