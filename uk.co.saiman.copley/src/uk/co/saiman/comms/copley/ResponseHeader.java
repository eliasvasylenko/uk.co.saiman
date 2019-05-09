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
 * This file is part of uk.co.saiman.copley.
 *
 * uk.co.saiman.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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