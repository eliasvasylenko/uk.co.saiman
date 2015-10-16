package uk.co.saiman.chemistry.isotope;

public class ShiftedNormalDistributionVarianceTest {
  public static void main(String[] args) {
    double x, ydx, sum = 0, variance = 0, offsetVariance = 0, b = 20, sigma = 100;
    for (int i = -1000000; i < 1000000; i++) {
      x = i / 1000.0;
      ydx = (1.0 / (Math.sqrt(2 * Math.PI) * sigma))
          * Math.pow(Math.E, -((x) * (x)) / (2 * sigma * sigma)) / 1000;
      sum += ydx;
      variance += ydx * x * x;
      offsetVariance += ydx * ((x - b) * (x - b));
    }
    System.out
        .println("This program is rubbish and doesn't even take command line arguments.");
    System.out.println();

    System.out.println("sum: " + sum);
    System.out.println("expected variance: " + sigma * sigma);
    System.out.println("actual variance: " + variance);
    System.out.println("offset of mean: " + b);
    System.out.println("variance with offset mean: " + offsetVariance);
    System.out.println("expected variance * offset ^ 2: " + (sigma * sigma + b
        * b));
  }
}
