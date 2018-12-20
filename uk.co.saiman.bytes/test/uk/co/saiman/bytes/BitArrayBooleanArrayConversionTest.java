/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.saiman.bytes.BitArray.fromBooleanArray;
import static uk.co.saiman.bytes.Endianness.BIG_ENDIAN;
import static uk.co.saiman.bytes.Endianness.LITTLE_ENDIAN;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BitArrayBooleanArrayConversionTest {
  static class Conversions {
    private final boolean[] booleans;
    private final long[] bigEndianLongs;
    private final long[] littleEndianLongs;

    public Conversions(boolean[] booleans, long[] bigEndianLongs, long[] littleEndianLongs) {
      this.booleans = booleans;
      this.bigEndianLongs = bigEndianLongs;
      this.littleEndianLongs = littleEndianLongs;
    }
  }

  public static Stream<Conversions> conversions() {
    return Stream
        .of(
            new Conversions(
                new boolean[] { true },
                new long[] { 0x8000000000000000L },
                new long[] { 1L }),

            new Conversions(new boolean[] { false }, new long[] { 0L }, new long[] { 0L }),

            new Conversions(
                new boolean[] { true, false },
                new long[] { 0x8000000000000000L },
                new long[] { 1L }),

            new Conversions(
                new boolean[] { false, true },
                new long[] { 0x4000000000000000L },
                new long[] { 2L }),

            new Conversions(
                new boolean[] {
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true },
                new long[] { -1L, 0xFF00000000000000L },
                new long[] { -1L, 0xFFL }),

            new Conversions(
                new boolean[] {
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false },
                new long[] { 0L, 0L },
                new long[] { 0L, 0L }));
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void booleanArrayRoundTripTest(Conversions conversions) {
    BitArray bits = fromBooleanArray(conversions.booleans);
    boolean[] result = bits.toBooleanArray();

    assertArrayEquals(conversions.booleans, result);
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void booleanArrayToBigEndianInternalRepresentationTest(Conversions conversions) {
    BitArray bits = fromBooleanArray(conversions.booleans);

    assertArrayEquals(conversions.bigEndianLongs, bits.getBits());
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void booleanArrayToBigEndianLongTest(Conversions conversions) {
    BitArray bits = fromBooleanArray(conversions.booleans);

    for (int i = 0; i < conversions.littleEndianLongs.length; i++) {
      long bigEndianResult = bits
          .slice(Long.SIZE * i, bits.length())
          .toNumber(Long.SIZE, BIG_ENDIAN);

      assertEquals(conversions.bigEndianLongs[i], bigEndianResult);
    }
  }

  @ParameterizedTest
  @MethodSource("conversions")
  public void booleanArrayToLittleEndianLongTest(Conversions conversions) {
    BitArray bits = fromBooleanArray(conversions.booleans);

    for (int i = 0; i < conversions.littleEndianLongs.length; i++) {
      long littleEndianResult = bits
          .slice(Long.SIZE * i, bits.length())
          .toNumber(Long.SIZE, LITTLE_ENDIAN);

      assertEquals(conversions.littleEndianLongs[i], littleEndianResult);
    }
  }
}
