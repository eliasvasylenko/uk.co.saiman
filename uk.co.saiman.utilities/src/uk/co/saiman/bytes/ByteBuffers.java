package uk.co.saiman.bytes;

import static java.lang.Character.digit;

import java.nio.ByteBuffer;

public final class ByteBuffers {
  private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  private ByteBuffers() {}

  public static ByteBuffer fromHexString(String object) {
    String hexString = object.toString().trim();

    if (!hexString.startsWith("0x")) {
      return null;
    }

    hexString = hexString.substring(2);

    int len = hexString.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < data.length; i++) {
      int j = i + i;
      data[i] = (byte) ((digit(hexString.charAt(j), 16) << 4) + digit(hexString.charAt(j + 1), 16));
    }

    return ByteBuffer.wrap(data);
  }

  public static String toHexString(ByteBuffer buffer) {
    byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);

    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }

    return "0x" + new String(hexChars);
  }
}
