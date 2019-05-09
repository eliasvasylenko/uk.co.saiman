package uk.co.saiman.comms.copley;

import static uk.co.saiman.comms.copley.CopleyController.CHECKSUM;
import static uk.co.saiman.comms.copley.CopleyController.WORD_SIZE;

import uk.co.saiman.bytes.conversion.DTO;
import uk.co.saiman.bytes.conversion.Offset;

@DTO
public class CommandHeader {
  public CommandHeader() {}

  public CommandHeader(int currentNode, CopleyOperationID operation, byte[] output) {
    this.currentNode = (byte) currentNode;
    this.messageSize = (byte) (output.length / WORD_SIZE);
    this.operation = operation.getCode();
    this.checksum = (byte) (CHECKSUM ^ this.currentNode ^ this.messageSize ^ this.operation);
    for (byte outputByte : output)
      this.checksum ^= outputByte;
    ;
  }

  @Offset(0)
  public byte currentNode;
  @Offset(8)
  public byte checksum;
  @Offset(16)
  public byte messageSize;
  @Offset(24)
  public byte operation;

  public CopleyOperationID operation() {
    return CopleyOperationID.getCanonicalOperation(operation);
  }

  public int messageBytes() {
    return messageSize * WORD_SIZE;
  }
}