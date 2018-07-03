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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static uk.co.saiman.bytes.BitArray.fromBooleanArray;
import static uk.co.saiman.bytes.Endianness.BIG_ENDIAN;
import static uk.co.saiman.bytes.Endianness.LITTLE_ENDIAN;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BitArrayBooleanArrayConversionTest {
  @Parameters
  public static Collection<Object[]> booleanArrays() {
    return asList(
        new Object[] {
            new boolean[] { true },
            new long[] { 0x8000000000000000L },
            new long[] { 1L } },

        new Object[] { new boolean[] { false }, new long[] { 0L }, new long[] { 0L } },

        new Object[] {
            new boolean[] { true, false },
            new long[] { 0x8000000000000000L },
            new long[] { 1L } },

        new Object[] {
            new boolean[] { false, true },
            new long[] { 0x4000000000000000L },
            new long[] { 2L } },

        new Object[] {
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
            new long[] { -1L, 0xFFL } },

        new Object[] {
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
            new long[] { 0L, 0L } });
  }

  private final boolean[] values;
  private final long[] bigEndian;
  private final long[] littleEndian;

  public BitArrayBooleanArrayConversionTest(
      boolean[] values,
      long[] bigEndian,
      long[] littleEndian) {
    this.values = values;
    this.bigEndian = bigEndian;
    this.littleEndian = littleEndian;
  }

  @Test
  public void booleanArrayRoundTripTest() {
    BitArray bits = fromBooleanArray(values);
    boolean[] result = bits.toBooleanArray();

    assertArrayEquals(values, result);
  }

  @Test
  public void booleanArrayToBigEndianInternalRepresentationTest() {
    BitArray bits = fromBooleanArray(values);

    assertArrayEquals(bigEndian, bits.getBits());
  }

  @Test
  public void booleanArrayToBigEndianLongTest() {
    BitArray bits = fromBooleanArray(values);

    for (int i = 0; i < littleEndian.length; i++) {
      long bigEndianResult = bits
          .slice(Long.SIZE * i, bits.length())
          .toNumber(Long.SIZE, BIG_ENDIAN);

      assertEquals(bigEndian[i], bigEndianResult);
    }
  }

  @Test
  public void booleanArrayToLittleEndianLongTest() {
    BitArray bits = fromBooleanArray(values);

    for (int i = 0; i < littleEndian.length; i++) {
      long littleEndianResult = bits
          .slice(Long.SIZE * i, bits.length())
          .toNumber(Long.SIZE, LITTLE_ENDIAN);

      assertEquals(littleEndian[i], littleEndianResult);
    }
  }
}
