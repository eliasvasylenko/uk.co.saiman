/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
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
