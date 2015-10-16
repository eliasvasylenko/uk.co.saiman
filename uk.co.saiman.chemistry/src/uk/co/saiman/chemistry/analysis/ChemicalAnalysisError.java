package uk.co.saiman.chemistry.analysis;

public class ChemicalAnalysisError extends Error {
  private static final long serialVersionUID = 6971607178671041381L;

  public ChemicalAnalysisError(String string) {
    super(string);
  }

  public ChemicalAnalysisError(Error error) {
    super(error);
  }

  public ChemicalAnalysisError(String string, Error error) {
    super(string, error);
  }
}
