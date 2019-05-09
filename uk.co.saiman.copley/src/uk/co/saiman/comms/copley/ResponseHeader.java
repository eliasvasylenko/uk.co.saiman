package uk.co.saiman.comms.copley;

import static uk.co.saiman.comms.copley.CopleyController.WORD_SIZE;

import uk.co.saiman.bytes.conversion.DTO;
import uk.co.saiman.bytes.conversion.Offset;

@DTO
public class ResponseHeader {
  public ResponseHeader() {}

  public ResponseHeader(byte[] output) {
    this.messageSize = (byte) (output.length / WORD_SIZE);
    this.errorCode = (byte) ErrorCode.SUCCESS.ordinal();
    this.checksum = (byte) (CopleyController.CHECKSUM ^ output.length);
    for (byte item : output)
      this.checksum ^= item;
  }

  public ResponseHeader(ErrorCode errorCode) {
    this.messageSize = (byte) 0;
    this.errorCode = (byte) errorCode.ordinal();
    this.checksum = (byte) CopleyController.CHECKSUM;
  }

  @Offset(0)
  @Deprecated
  public byte reserved;
  @Offset(8)
  public byte checksum;
  @Offset(16)
  public byte messageSize;
  @Offset(24)
  public byte errorCode;

  public ErrorCode errorCode() {
    return ErrorCode.values()[errorCode];
  }

  public int messageBytes() {
    return messageSize * WORD_SIZE;
  }
}