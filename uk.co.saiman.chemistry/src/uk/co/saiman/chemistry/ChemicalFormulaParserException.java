package uk.co.saiman.chemistry;

import uk.co.saiman.chemistry.analysis.ChemicalAnalysisException;

public class ChemicalFormulaParserException extends ChemicalAnalysisException {
  private static final long serialVersionUID = 1L;

  public ChemicalFormulaParserException() {
    super("Unexpected error when parsing molecular formula.");
  }

  public ChemicalFormulaParserException(String message) {
    super(message);
  }
}