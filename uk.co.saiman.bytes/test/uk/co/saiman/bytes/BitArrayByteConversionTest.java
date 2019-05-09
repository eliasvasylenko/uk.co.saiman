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

public class BitArrayByteConversionTest {
  static class IntConversions {
    private final byte number;
    private final long internalRepresentation;

    public IntConversions(byte number, long internalRepresentation) {
      this.number = number;
      this.internalRepresentation = internalRepresentation;
    }
  }

  public static Stream<IntConversions> conversions() {
    return Stream
        .of(

            new IntConversions((byte) 1, 0x0100000000000000L),

            new IntConversions((byte) 0, 0L),

            new IntConversions((byte) 2, 0x0200000000000000L),

            new IntConversions((byte) 127, 0x7F00000000000000L),

            new IntConversions((byte) Math.pow(2, 6), 0x4000000000000000L),

            new IntConversions((byte) -1, 0xFF00000000000000L)

        );
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void byteBigEndianRoundTripTest(IntConversions conversions) {
    BitArray bits = BitArray.fromByte(conversions.number);
    byte result = bits.toByte();

    assertEquals(conversions.number, result);
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void byteToBigEndianInternalRepresentationTest(IntConversions conversions) {
    BitArray bits = BitArray.fromByte(conversions.number);

    assertEquals(conversions.internalRepresentation, bits.getBits()[0]);
    assertEquals(Byte.SIZE, bits.length());
  }
}
