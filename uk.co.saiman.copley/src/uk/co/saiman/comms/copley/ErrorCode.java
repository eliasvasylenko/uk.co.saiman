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

public enum ErrorCode {
  SUCCESS,
  TOO_MUCH_DATA,
  CHECKSUM_ERROR,
  ILLEGAL_OP_CODE,
  INSUFFICIENT_DATA,
  UNEXPECTED_DATA,
  ERROR_ERASING_FLASH,
  ERROR_WRITING_FLASH,
  ILLEGAL_MEMORY_PAGE,
  UNKNOWN_VARIABLE_ID,
  OUT_OF_RANGE,
  ILLEGAL_WRITE,
  INVALID_TRACE_CHANNEL,
  INVALID_TRACE_VARIABLE,
  INVALID_OPERATING_MODE,
  VARIABLE_NOT_IN_PAGE,
  NOT_CAN_MASTER,
  FLASH_PAGE_CRC_ERROR,
  ILLEGAL_MOVE_START,
  ILLEGAL_VELOCITY_LIMIT,
  ILLEGAL_ACCELERATION_LIMIT,
  ILLEGAL_DECELERATION_LIMIT,
  ILLEGAL_JERK_LIMIT,
  TRAJECTORY_BUFFER_UNDERFLOW,
  TRAJECTORY_BUFFER_OVERFLOW,
  BAD_TRAJECTORY_MODE,
  CVM_LOCATION_UNAVAILABLE,
  ILLEGAL_DURING_CVM,
  CVM_EXCEEDS_SIZE_LIMIT,
  FILE_SYSTEM_ERROR,
  PROGRAM_DOES_NOT_EXIST,
  INVALID_NODE_ID,
  CAN_COMMS_FAILURE,
  ASCII_PARSE_ERROR,
  INTERNAL_ERROR,
  ILLEGAL_FILESYSTEM_MODIFICATION,
  ILLEGAL_AXIS_NUMBER,
  INVALID_FPGA_DATA,
  INITIALIZING_FPGA_FAILED,
  CONFIGURING_FPGA_FAILED,
  FILE_EXISTS,
  NO_FREE_FILES,
  FILE_DOES_NOT_EXIST,
  OUT_OF_SPACE,
  INVALID_FILE_FORMAT,
  UNEXPECTED_EOF,
  ERROR_SENDING_TO_ENCODER,
  ILLEGAL_OPERATION_FOR_PORT,
  CANNOT_CALCULATE_FILTER,
  CVM_COMMAND_PROTECTED,
  TIMEOUT
}
