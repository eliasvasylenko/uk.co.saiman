package uk.co.saiman.chemistry.analysis;

public class ChemicalAnalysisException extends RuntimeException {
	private static final long serialVersionUID = -7165565189422539744L;

	public ChemicalAnalysisException(String message) {
		super(message);
	}

	public ChemicalAnalysisException(Exception exception) {
		super(exception);
	}

	public ChemicalAnalysisException(String message, Exception exception) {
		super(message, exception);
	}
}
