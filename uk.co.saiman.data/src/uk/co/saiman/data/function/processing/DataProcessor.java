package uk.co.saiman.data.function.processing;

import java.util.function.Function;

import javax.measure.Quantity;

import uk.co.saiman.data.function.ArraySampledContinuousFunction;
import uk.co.saiman.data.function.SampledContinuousFunction;

/**
 * Spectrum processors must be immutable.
 * 
 * @author Elias N Vasylenko
 */
public interface DataProcessor {
  /**
   * Define a processor which operates solely on sample positions in the range of
   * a function. In other words, a processor which does not modify or inspect
   * sample positions in the domain of a function.
   * <p>
   * Each valid index into the produced array must correspond to a valid index
   * into the original array at the given offset. This means that the function may
   * produce an array which is shorter, but never longer, than the original. This
   * allows for e.g. convolution filters which shorten the data.
   * <p>
   * The derived data processor then maps each range value in the produced array
   * to a domain value in the given function by way of the offset.
   * 
   * @param process
   *          a function from a set of range values to another set of range values
   * @param offset
   *          the indexing offset to map indices in the produced array to
   *          corresponding indices in the original array
   * @return a data processor implementing the given behaviour
   */
  static DataProcessor arrayProcessor(Function<double[], double[]> process, int offset) {
    return new DataProcessor() {
      @Override
      public <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
          SampledContinuousFunction<UD, UR> data) {
        double[] array = new double[data.getDepth()];
        for (int i = 0; i < array.length; i++)
          array[i] = data.range().getSample(i);

        return new ArraySampledContinuousFunction<>(
            data.domain(),
            data.range().getUnit(),
            process.apply(array));
      }
    };
  }

  <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
      SampledContinuousFunction<UD, UR> data);

  default DataProcessor andThen(DataProcessor next) {
    DataProcessor thisProcessor = this;
    return new DataProcessor() {
      @Override
      public <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
          SampledContinuousFunction<UD, UR> data) {
        return next.process(thisProcessor.process(data));
      }
    };
  }

  static DataProcessor identity() {
    return new DataProcessor() {
      @Override
      public <UD extends Quantity<UD>, UR extends Quantity<UR>> SampledContinuousFunction<UD, UR> process(
          SampledContinuousFunction<UD, UR> data) {
        return data;
      }
    };
  }
}
