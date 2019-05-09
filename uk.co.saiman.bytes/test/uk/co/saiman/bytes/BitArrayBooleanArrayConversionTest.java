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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static uk.co.saiman.bytes.BitArray.fromBooleanArray;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BitArrayBooleanArrayConversionTest {
  static class Conversions {
    private final boolean[] booleans;
    private final long[] internalRepresentation;

    public Conversions(boolean[] booleans, long[] internalRepresentation) {
      this.booleans = booleans;
      this.internalRepresentation = internalRepresentation;
    }
  }

  public static Stream<Conversions> conversions() {
    return Stream
        .of(
            new Conversions(new boolean[] { true }, new long[] { 0x8000000000000000L }),

            new Conversions(new boolean[] { false }, new long[] { 0L }),

            new Conversions(new boolean[] { true, false }, new long[] { 0x8000000000000000L }),

            new Conversions(new boolean[] { false, true }, new long[] { 0x4000000000000000L }),

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
                new long[] { -1L, 0xFF00000000000000L }),

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

    assertArrayEquals(conversions.internalRepresentation, bits.getBits());
  }
}
