package uk.co.saiman.eclipse.ui;

import java.util.Set;

import uk.co.saiman.data.format.DataFormat;

public interface TransferFormat<T> {
  /**
   * The transfer modes which a data target may accept in this format.
   * <p>
   * Note that this only determines the accepted transfer modes at the
   * <em>target</em>, and has no bearing on which transfer modes may be supported
   * by the <em>source</em>.
   * 
   * @return a set of the accepted transfer modes
   */
  Set<TransferMode> transferModes();

  DataFormat<T> dataFormat();
}
