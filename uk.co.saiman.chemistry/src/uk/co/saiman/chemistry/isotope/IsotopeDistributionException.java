package uk.co.saiman.chemistry.isotope;

import uk.co.saiman.chemistry.analysis.ChemicalAnalysisException;

public class IsotopeDistributionException extends ChemicalAnalysisException {
  private static final long serialVersionUID = 1L;

  public IsotopeDistributionException() {
    super("Unexpected exception when calculating isotope distribution.");
  }

  public IsotopeDistributionException(String message) {
    super(message);
  }

  public IsotopeDistributionException(Exception exception) {
    super("Exception encountered while calculating isotope distribution: "
        + exception.getLocalizedMessage());
  }
}