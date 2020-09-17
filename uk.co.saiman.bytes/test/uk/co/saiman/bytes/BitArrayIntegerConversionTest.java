/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.bytes.
 *
 * uk.co.saiman.bytes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.bytes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.bytes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BitArrayIntegerConversionTest {
  static class IntConversions {
    private final int number;
    private final long internalRepresentation;

    public IntConversions(int number, long internalRepresentation) {
      this.number = number;
      this.internalRepresentation = internalRepresentation;
    }
  }

  public static Stream<IntConversions> conversions() {
    return Stream
        .of(

            new IntConversions(1, 0x0000000100000000L),

            new IntConversions(0, 0L),

            new IntConversions(2, 0x0000000200000000L),

            new IntConversions(255, 0x000000FF00000000L),

            new IntConversions((int) Math.pow(2, 30), 0x4000000000000000L),

            new IntConversions(-1, 0xFFFFFFFF00000000L)

        );
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void intBigEndianRoundTripTest(IntConversions conversions) {
    BitArray bits = BitArray.fromInt(conversions.number);
    int result = bits.toInt();

    assertEquals(conversions.number, result);
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void intToBigEndianInternalRepresentationTest(IntConversions conversions) {
    BitArray bits = BitArray.fromInt(conversions.number);

    assertEquals(conversions.internalRepresentation, bits.getBits()[0]);
    assertEquals(Integer.SIZE, bits.length());
  }
}
