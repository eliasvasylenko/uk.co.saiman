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

import static uk.co.saiman.bytes.ByteBuffers.toPrefixedHexString;
import static uk.co.saiman.bytes.Endianness.BIG_ENDIAN;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A bit array has no intrinsic notion of endianness. Endianness is a component
 * of the <em>interpretation</em> of the binary data, and a bit array is simply
 * an ordered sequence of bits with no further semantics.
 * <p>
 * For the purposes of discussion, bit ordering is notionally held to start on
 * the "left" and end on the "right". That is to say, the address index of zero
 * is referred to as the leftmost bit.
 * 
 * @author Elias N Vasylenko
 */
public class BitArray {
  private static final long FILLED_LONG = -1L;

  private final long[] bits;
  private final int length;

  /**
   * Create a big-endian bit array of the given length, initialized with 0 fill.
   * 
   * @param length
   *          the number of bits
   */
  public BitArray(int length) {
    this.bits = new long[minimumContainingUnits(length, Long.SIZE)];
    this.length = length;
  }

  protected BitArray(long[] bits, int length) {
    this.bits = bits;
    this.length = length;
  }

  void clearTail() {
    int bitInLastLong = ((length + Long.SIZE - 1) % Long.SIZE) + 1;
    for (int i = bitInLastLong; i < Long.SIZE; i++) {
      bits[bits.length - 1] &= ~getLongMask(i);
    }
  }

  static int minimumContainingUnits(int bits, int unitSize) {
    return bits / unitSize + ((bits % unitSize > 0) ? 1 : 0);
  }

  @Override
  public String toString() {
    return toPrefixedHexString(toByteBuffer()).toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof BitArray))
      return false;
    BitArray that = (BitArray) obj;
    return Arrays.equals(this.bits, that.bits) && this.length == that.length;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bits) ^ length;
  }

  long[] getBits() {
    return Arrays.copyOf(bits, bits.length);
  }

  /**
   * @return the number of bits
   */
  public int length() {
    return length;
  }

  public boolean isEmpty() {
    return length == 0;
  }

  int getLongIndex(int index) {
    return index / Long.SIZE;
  }

  long getLongMask(int index) {
    return 0x01L << (Long.SIZE - 1 - index % Long.SIZE);
  }

  void validateIndex(int index) {
    if (index < 0 || index >= length)
      throw new IndexOutOfBoundsException(Integer.toString(index));
  }

  public boolean get(int index) {
    validateIndex(index);
    return (bits[getLongIndex(index)] & getLongMask(index)) != 0;
  }

  protected BitArray set(int index, boolean value) {
    validateIndex(index);
    if (value)
      bits[getLongIndex(index)] |= getLongMask(index);
    else
      bits[getLongIndex(index)] &= ~getLongMask(index);
    return this;
  }

  /**
   * Derive a bit array from the receiver, whose contents are modified according
   * to the given value for the given bit index.
   * 
   * @param index
   *          the bit index
   * @param value
   *          the value for the bit in the derived array
   * @return the derived bit array
   */
  public BitArray with(int index, boolean value) {
    return new BitArray(bits, length).set(index, value);
  }

  /**
   * Derive a bit array from the receiver, whose contents are modified according
   * to the given value for the given bit index.
   * 
   * @param from
   *          the start bit index
   * @param to
   *          the end bit index
   * @param value
   *          the value for the bit in the derived array
   * @return the derived bit array
   */
  public BitArray with(int from, int to, boolean value) {
    BitArray array = new BitArray(bits, length);
    for (int i = from; i < to; i++) {
      array.set(i, value);
    }
    return array;
  }

  /**
   * Derive a new bit array of the given length. Changes are made by truncating
   * from or appending to the end of the sequence.
   * 
   * @param length
   *          the new length
   * @return a derived bit array
   */
  public BitArray resize(int length) {
    int longs = minimumContainingUnits(length, Long.SIZE);
    BitArray resized = new BitArray(Arrays.copyOf(bits, longs), length);

    resized.clearTail();

    return resized;
  }

  public BitArray append(BitArray bits) {
    BitArray appended = resize(length() + bits.length());

    for (int i = 0; i < bits.length(); i++) {
      appended.set(i + length(), bits.get(i));
    }

    return appended;
  }

  public BitArray prepend(BitArray bits) {
    return bits.append(this);
  }

  public BitArray slice(int from, int to) {
    BitArray sliced = new BitArray(to - from);

    int fromBounded = from > 0 ? from : 0;
    int toBounded = to < length ? to : length;

    for (int i = fromBounded; i < toBounded; i++) {
      sliced.set(i - from, get(i));
    }

    return sliced;
  }

  public BitArray splice(int at, BitArray bitArray) {
    int length = Math.max(length(), at + bitArray.length());
    BitArray spliced = new BitArray(length);

    for (int i = 0; i < bitArray.length(); i++) {
      spliced.set(i + at, bitArray.get(i));
    }

    return spliced;
  }

  public BitArray remove(int from, int to) {
    int range = to - from;
    BitArray removed = resize(length() - range);

    for (int i = to; i < length(); i++) {
      removed.set(i - range, get(i));
    }

    return removed;
  }

  public BitArray insert(int at, BitArray bitArray) {
    BitArray inserted = resize(length() + bitArray.length());

    for (int i = 0; i < bitArray.length(); i++) {
      inserted.set(i + at, bitArray.get(i));
    }
    for (int i = at; i < length(); i++) {
      inserted.set(i + bitArray.length(), get(i));
    }

    return inserted;
  }

  public BitArray reverse() {
    BitArray reversed = new BitArray(length());

    for (int i = 0; i < length; i++) {
      reversed.set(i, get(length - i - 1));
    }

    return reversed;
  }

  public BitArray invert() {
    BitArray inverted = new BitArray(length());

    for (int i = 0; i < length; i++) {
      inverted.set(i, !get(i));
    }

    return inverted;
  }

  public static BitArray fromByteBuffer(ByteBuffer bytes) {
    byte[] array = new byte[bytes.remaining()];
    bytes.get(array);
    return fromByteArray(array);
  }

  public ByteBuffer toByteBuffer() {
    byte[] array = toByteArray();
    return ByteBuffer.wrap(Arrays.copyOf(array, array.length));
  }

  /*
   * Byte array:
   */

  public static BitArray fromByteArray(byte[] bytes) {
    return fromByteArray(bytes, BIG_ENDIAN);
  }

  public static BitArray fromByteArray(byte[] bytes, Endianness endianness) {
    BitArray bits = new BitArray(bytes.length * Byte.SIZE);

    int bytesPerLong = Long.SIZE / Byte.SIZE;

    for (int i = 0; i < bytes.length; i++) {
      int longIndex = i / Long.SIZE;
      int byteIndex = i % bytesPerLong;
      bits.bits[i] = (byte) (bits.bits[longIndex] >> ((bytesPerLong - 1 - byteIndex) * Byte.SIZE));
    }

    throw new UnsupportedOperationException();
  }

  public byte[] toByteArray() {
    return toByteArray(BIG_ENDIAN);
  }

  public byte[] toByteArray(Endianness endianness) {
    BitArray bits = endianness == BIG_ENDIAN ? this : reverse();

    byte[] bytes = new byte[minimumContainingUnits(bits.length(), Byte.SIZE)];

    int bytesPerLong = (Long.SIZE / Byte.SIZE);

    for (int i = 0; i < bytes.length; i++) {
      int longIndex = i / Long.SIZE;
      int byteIndex = i % bytesPerLong;
      bytes[i] = (byte) (this.bits[longIndex] >> ((bytesPerLong - 1 - byteIndex) * Byte.SIZE));
    }

    return bytes;
  }

  public static BitArray fromByte(byte value) {
    return fromNumber(value, Byte.SIZE);
  }

  public byte toByte() {
    return (byte) toNumber(Byte.SIZE);
  }

  public static BitArray fromShort(short value) {
    return fromNumber(value, Short.SIZE);
  }

  public short toShort() {
    return (short) toNumber(Short.SIZE);
  }

  public static BitArray fromInt(int value) {
    return fromNumber(value, Integer.SIZE);
  }

  public int toInt() {
    return (int) toNumber(Integer.SIZE);
  }

  public static BitArray fromLong(long value) {
    return fromNumber(value, Long.SIZE);
  }

  public long toLong() {
    return toNumber(Long.SIZE);
  }

  /*
   * Number:
   */

  public static BitArray fromNumber(long value, int size) {
    return fromNumber(value, size, BIG_ENDIAN);
  }

  public static BitArray fromNumber(long value, int size, Endianness endianness) {
    long longValue = 0;

    switch (endianness) {
    case BIG_ENDIAN:
      longValue = (long) value << (Long.SIZE - size);
    case LITTLE_ENDIAN:
      longValue = Long.reverse(value & (FILLED_LONG >> (Long.SIZE - size)));
    }

    return new BitArray(new long[] { longValue }, size);
  }

  public long toNumber(int size) {
    return toNumber(size, BIG_ENDIAN);
  }

  public long toNumber(int size, Endianness endianness) {
    if (!isEmpty()) {
      switch (endianness) {
      case BIG_ENDIAN:
        return (bits[0] >> (Long.SIZE - size));
      case LITTLE_ENDIAN:
        return Long.reverse(bits[0]) & (FILLED_LONG >> (Long.SIZE - size));
      }
    }
    throw new IndexOutOfBoundsException("No value available at " + 0);
  }

  /*
   * Boolean:
   */

  public static BitArray fromBooleanArray(boolean[] array) {
    BitArray bits = new BitArray(array.length);
    for (int i = 0; i < array.length; i++) {
      bits.set(i, array[i]);
    }
    return bits;
  }

  public boolean[] toBooleanArray() {
    boolean[] array = new boolean[length];
    for (int i = 0; i < length; i++) {
      array[i] = get(i);
    }
    return array;
  }

  public Stream<Boolean> stream() {
    // TODO Auto-generated method stub
    return null;
  }
}
