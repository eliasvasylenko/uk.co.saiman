/*
 * Copyright (C) 2015 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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