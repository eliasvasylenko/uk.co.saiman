package uk.co.saiman.experiment.processing;

import org.junit.Assert;
import org.junit.Test;

public class ConvolutionTest {
  private static final double[] EMPTY = new double[] {};
  private static final double[] UNIT = new double[] { 1 };
  private static final double[] ONE = new double[] { 7 };
  private static final double[] TWO = new double[] { 5, 13 };
  private static final double[] THREE = new double[] { 3, 11, 17 };

  @Test(expected = IllegalArgumentException.class)
  public void emptyConvolutionVectorTest() {
    Convolution.process(UNIT, EMPTY);
  }

  @Test(expected = NegativeArraySizeException.class)
  public void emptyDataTest() {
    double[] result = Convolution.process(EMPTY, THREE);
    Assert.assertArrayEquals(EMPTY, result, 0);
  }

  @Test
  public void smallerDataThanConvolutionVectorTest() {
    double[] result = Convolution.process(TWO, THREE);
    double[] resultOffset0 = Convolution.process(TWO, THREE, 0);
    double[] resultOffset1 = Convolution.process(TWO, THREE, 1);
    double[] resultOffset2 = Convolution.process(TWO, THREE, 2);

    Assert.assertArrayEquals(EMPTY, result, 0);
    Assert.assertArrayEquals(new double[] { 379, 403 }, resultOffset0, 0);
    Assert.assertArrayEquals(new double[] { 291, 379 }, resultOffset1, 0);
    Assert.assertArrayEquals(new double[] { 155, 291 }, resultOffset2, 0);
  }
}
