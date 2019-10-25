package uk.co.saiman.maldi.spectrum;

import static uk.co.saiman.measurement.Quantities.quantityFormat;
import static uk.co.saiman.measurement.Units.dalton;
import static uk.co.saiman.state.Accessor.intAccessor;
import static uk.co.saiman.state.Accessor.stringAccessor;

import javax.measure.Quantity;
import javax.measure.quantity.Mass;

import uk.co.saiman.experiment.variables.Variable;

public final class MaldiSpectrumConstants {
  private MaldiSpectrumConstants() {}

  public static final String SPECTRUM_EXECUTOR = "uk.co.saiman.maldi.executor.spectrum";

  public static final String SPECTRUM_MASS_LIMIT_ID = "uk.co.saiman.maldi.variable.spectrum.masslimit";
  public static final Variable<Quantity<Mass>> SPECTRUM_MASS_LIMIT = new Variable<>(
      SPECTRUM_MASS_LIMIT_ID,
      stringAccessor()
          .map(
              string -> quantityFormat().parse(string).asType(Mass.class).to(dalton().getUnit()),
              quantityFormat()::format));

  public static final String SPECTRUM_ACQUISITION_COUNT_ID = "uk.co.saiman.maldi.variable.spectrum.acquisitioncount";
  public static final Variable<Integer> SPECTRUM_ACQUISITION_COUNT = new Variable<>(
      SPECTRUM_ACQUISITION_COUNT_ID,
      intAccessor());
}
