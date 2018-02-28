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

import static java.nio.ByteBuffer.allocate;
import static uk.co.saiman.bytes.ByteBuffers.toHexString;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * TODO flatten into long array like BitSet. Shame the API for that is so awful
 * and it doesn't understand size.
 * 
 * In all conversion operations, bit 0 represents the least significant bit and
 * all representations are big endian with both bytes and bits.
 * 
 * @author Elias N Vasylenko
 */
public class BitArray {
  byte[] bytes;
  int length;

  /**
   * Create a bit array of the given length, initialized with 0 fill
   * 
   * @param length
   *          the number of bits
   */
  public BitArray(int length) {
    this.bytes = new byte[minimumContainingBytes(length)];
    this.length = length;
  }

  protected BitArray(byte[] bytes) {
    this.bytes = Arrays.copyOf(bytes, bytes.length);
    this.length = bytes.length * Byte.SIZE;
  }

  protected BitArray(BitArray base) {
    this.bytes = base.bytes;
    this.length = base.length;
  }

  int minimumContainingBytes(int bits) {
    return bits / Byte.SIZE + ((bits % Byte.SIZE > 0) ? 1 : 0);
  }

  @Override
  public String toString() {
    return toHexString(toByteBuffer()).toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof BitArray))
      return false;
    BitArray that = (BitArray) obj;
    return Arrays.equals(bytes, that.bytes) && length == that.length;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes) ^ length;
  }

  /**
   * @return the number of bits
   */
  public int length() {
    return length;
  }

  private int getByteIndex(int index) {
    return bytes.length - 1 - index / Byte.SIZE;
  }

  private int getBitMask(int index) {
    return 0x01 << (index % Byte.SIZE);
  }

  private void validateIndex(int index) {
    if (index < 0 || index >= length)
      throw new IndexOutOfBoundsException(Integer.toString(index));
  }

  /**
   * @param index
   *          the bit index, where 0 is the least significant bit
   * @return the value of the bit at the given index
   */
  public boolean get(int index) {
    validateIndex(index);
    return (bytes[getByteIndex(index)] & getBitMask(index)) > 0;
  }

  protected BitArray set(int index, boolean value) {
    validateIndex(index);
    if (value)
      bytes[getByteIndex(index)] |= getBitMask(index);
    else
      bytes[getByteIndex(index)] &= ~getBitMask(index);
    return this;
  }

  /**
   * Derive a bit array from the receiver, whose contents are modified according
   * to the given value for the given bit index.
   * 
   * @param index
   *          the bit index, where 0 is the least significant bit
   * @param value
   *          the value for the bit in the derived array
   * @return the derived bit array
   */
  public BitArray with(int index, boolean value) {
    return new BitArray(this).set(index, value);
  }

  /**
   * Derive a new bit array of the given length.
   * <p>
   * If the given length is positive, changes are made by truncating from or
   * appending to the end of the most significant bit. If the given length is
   * negative, changes are made by truncating from or prepending to the least
   * significant bit.
   * 
   * @param length
   *          the new length
   * @return a derived bit array
   */
  public BitArray resize(int length) {
    boolean positive = length > 0;
    length = positive ? length : -length;
    int difference = length - length();

    if (difference > 0) {
      return positive ? append(new BitArray(difference)) : prepend(new BitArray(difference));

    } else {
      BitArray tail = new BitArray(length);
      difference = positive ? 0 : -difference;

      for (int i = 0; i < length; i++) {
        tail.set(i, get(i + difference));
      }

      return tail;
    }
  }

  /**
   * @param size
   *          the
   * @return a derived bit array, truncated or 0 filled to the new length
   */
  public BitArray trim(int size) {
    if (size < 0)
      return resize(-length() - size);
    else
      return resize(length() - size);
  }

  public BitArray append(BitArray bits) {
    BitArray appended = new BitArray(length() + bits.length());

    for (int i = 0; i < length(); i++) {
      appended.set(i, get(i));
    }
    for (int i = 0; i < bits.length(); i++) {
      appended.set(i + length(), bits.get(i));
    }

    return appended;
  }

  public BitArray prepend(BitArray bits) {
    return bits.append(this);
  }

  public BitArray splice(int at, BitArray bitArray) {
    BitArray spliced = new BitArray(this);

    for (int i = 0; i < bitArray.length(); i++) {
      spliced.set(i + at, bitArray.get(i));
    }

    return spliced;
  }

  public BitArray remove(int from, int to) {
    int range = to - from;
    BitArray removed = new BitArray(length() - range);

    for (int i = 0; i < from; i++) {
      removed.set(i, get(i));
    }
    for (int i = to; i < length(); i++) {
      removed.set(i - range, get(i));
    }

    return removed;
  }

  public BitArray insert(int at, BitArray bitArray) {
    BitArray inserted = new BitArray(length() + bitArray.length());

    for (int i = 0; i < at; i++) {
      inserted.set(i, get(i));
    }
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

  public static BitArray fromByteArray(byte[] bytes) {
    return new BitArray(bytes);
  }

  public byte[] toByteArray() {
    return Arrays.copyOf(bytes, bytes.length);
  }

  public static BitArray fromByteBuffer(ByteBuffer bytes) {
    byte[] array = new byte[bytes.remaining()];
    bytes.get(array);
    return fromByteArray(array);
  }

  public ByteBuffer toByteBuffer() {
    return ByteBuffer.wrap(Arrays.copyOf(bytes, bytes.length));
  }

  public static BitArray fromByte(byte value) {
    return fromByteArray(new byte[] { value });
  }

  public byte toByte() {
    return bytes.length == 0 ? 0 : bytes[bytes.length - 1];
  }

  public static BitArray fromInt(int value) {
    ByteBuffer bytes = allocate(Integer.BYTES).putInt(value);
    bytes.flip();
    return fromByteBuffer(bytes);
  }

  public int toInt() {
    return resize(Integer.SIZE).toByteBuffer().getInt();
  }

  public boolean[] toBooleanArray() {
    boolean[] array = new boolean[length];
    for (int i = 0; i < length; i++) {
      array[i] = get(i);
    }
    return array;
  }
}
