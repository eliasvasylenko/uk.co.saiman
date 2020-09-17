/**
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;

using OpenNETCF.IO.Ports;

namespace MiniSIMS.Instrument.Interface
{
    public class CopleyMotorInterface : MotorControllerInterface
    {
        private SerialPort copleyPort;
        private bool connected = false;
        private bool initialising = false;

        private const int xAxis = 0;
        private const int yAxis = 1;

        private CopleyInterfacePacket clearFaults;

        private CopleyInterfacePacket setTrajectoryModeAbsoluteX;
        private CopleyInterfacePacket turnOnAmplifierX;
        private CopleyInterfacePacket turnOffAmplifierX;
        private CopleyInterfacePacket startHomingX;
        private CopleyInterfacePacket startMoveX;
        private CopleyInterfacePacket readPositionX;

        private CopleyInterfacePacket setTrajectoryModeAbsoluteY;
        private CopleyInterfacePacket turnOnAmplifierY;
        private CopleyInterfacePacket turnOffAmplifierY;
        private CopleyInterfacePacket startHomingY;
        private CopleyInterfacePacket startMoveY;
        private CopleyInterfacePacket readPositionY;

        private int lastGoodXPosition = 0;
        private int lastGoodYPosition = 0;

        private int responseTimeout = 15000;

        public CopleyMotorInterface()
        {
            CreateCommandPackets();
        }

        //Make sure we close the port at all costs!
        ~CopleyMotorInterface()
        {
            if (copleyPort != null)
            {
                if (copleyPort.IsOpen)
                {
                    copleyPort.Close();
                }
                copleyPort.Dispose();
            }
        }

        private void CreateCommandPackets()
        {
            byte[] value = new byte[2];

            //Set Trajectory Mode (0x0000 = absolute position mode)
            value[0] = 0x00;
            value[1] = 0x00;
            setTrajectoryModeAbsoluteX = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.trajectoryProfileMode, value);
            setTrajectoryModeAbsoluteY = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.trajectoryProfileMode, value);

            //Turn on amplifiers.
            value[0] = 0x00;
            value[1] = 0x15;
            turnOnAmplifierX = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.amplifierState, value);
            turnOnAmplifierY = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.amplifierState, value);

            //Turn off amplifiers.
            value[0] = 0x00;
            value[1] = 0x00; //In servo mode, the position loop is driven by the trajectory generator
            turnOffAmplifierX = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.amplifierState, value);
            turnOffAmplifierY = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.amplifierState, value);

            //Start Homing Procedure
            startHomingX = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.trajectoryCommand, CopleyVariableIdentifiers.startHoming, null);
            startHomingY = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.trajectoryCommand, CopleyVariableIdentifiers.startHoming, null);

            //Start move command.
            startMoveX = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.trajectoryCommand, CopleyVariableIdentifiers.startMove, null);
            startMoveY = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.trajectoryCommand, CopleyVariableIdentifiers.startMove, null);

            //Read position command.
            readPositionX = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.getVariable, CopleyVariableIdentifiers.actualPosition, null);
            readPositionY = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.getVariable, CopleyVariableIdentifiers.actualPosition, null);

            //Clear latched faults command.            
            byte[] clearAllFaults = new byte[4];
            clearAllFaults[0] = 0xFF;
            clearAllFaults[0] = 0xFF;
            clearAllFaults[0] = 0xFF;
            clearAllFaults[0] = 0xFF;
            clearFaults = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.latchedFaultRegister, clearAllFaults);
        }

        private bool IsCopleyController()
        {
            bool isCopleyController = false;

            if (!copleyPort.IsOpen)
            {
                try
                {
                    copleyPort.Open();

                    if (copleyPort.IsOpen)
                    {
                        copleyPort.DiscardInBuffer();

                        isCopleyController = PingOK();
                    }
                }
                catch (Exception uae)
                {
                    Console.WriteLine("MotorController: 'UnauthorizedAccessException' trying to open port.");
                    Console.WriteLine(uae.Message);
                    isCopleyController = false;
                }
            }

            return isCopleyController;
        }

        bool MotorControllerInterface.Connect(string suppliedPortName)
        {
            if (connected)
            {
                initialising = false;
                return true;
            }

            initialising = true;

            if (string.IsNullOrEmpty(suppliedPortName))
            {
                //Try to find out which port the Copley controller is connected to.
                string[] portNames = SerialPort.GetPortNames();
                foreach (string portName in portNames)
                {
                    copleyPort = new SerialPort(portName, 9600, Parity.None, 8, StopBits.One);
                    copleyPort.Handshake = Handshake.None;
                    copleyPort.ReceivedBytesThreshold = 1;

                    if (IsCopleyController())
                    {
                        connected = true;
                        break;
                    }
                    else
                    {
                        if (copleyPort.IsOpen)
                        {
                            copleyPort.Close();
                        }
                        copleyPort.Dispose();
                    }
                }
            }
            else
            {
                //Use the supplied port.
                copleyPort = new SerialPort(suppliedPortName, 9600, Parity.None, 8, StopBits.One);
                copleyPort.Handshake = Handshake.None;
                copleyPort.ReceivedBytesThreshold = 1;

                connected = IsCopleyController();

                if (!connected)
                {
                    copleyPort.Close();
                    copleyPort.Dispose();
                }
            }

            initialising = false;

            if (connected)
            {
                WritePacket(clearFaults, responseTimeout);
            }

            return connected;
        }

        void MotorControllerInterface.Disconnect()
        {
            if (copleyPort != null)
            {
                if (copleyPort.IsOpen)
                {
                    copleyPort.Close();
                }
                copleyPort.Dispose();
            }
        }

        private bool PingOK()
        {
            CopleyInterfacePacket pingPacket = CopleyMotorCommands.CreatePacket(CopleyOpcodes.operatingMode);

            byte[] expectedPingResponse = new byte[6];
            expectedPingResponse[0] = 0x00;
            expectedPingResponse[1] = 0x5B;
            expectedPingResponse[2] = 0x01;
            expectedPingResponse[3] = 0x00;
            expectedPingResponse[4] = 0x00;
            expectedPingResponse[5] = 0x00;

            //Send the ping packet and convert the response into a byte array.
            byte[] responseByteArray = CopleyMotorCommands.ToByteArray(WritePacket(pingPacket, 2000));

            //Compare the actual response to the expected response.  If they match,
            //we're communicating with the correct device.
            return responseByteArray.SequenceEqual(expectedPingResponse);
        }

        bool MotorControllerInterface.Connected()
        {
            return PingOK();
        }

        private CopleyInterfacePacket WritePacket(CopleyInterfacePacket commandPacket, int timeout)
        {
            if (commandPacket.data == null)
            {
                commandPacket.data = new byte[0];
            }

            if (!connected && !initialising)
            {
                return commandPacket;
            }

            byte[] outBuffer = CopleyMotorCommands.ToByteArray(commandPacket);

            copleyPort.Write(outBuffer, 0, outBuffer.Length);

            //We should probably use the asynchronous 'ReceivedEvent' here instead,
            //but simply sleeping seems to work well enough for the time being.
            int total = 0;
            while ((copleyPort.BytesToRead < 4) && (total < timeout))
            {
                Thread.Sleep(100);
                total += 100;
            }

            List<byte> inBuffer = new List<byte>();
            while (copleyPort.BytesToRead > 0)
            {
                inBuffer.Add((byte)copleyPort.ReadByte());
            }

            return CopleyMotorCommands.ToCommandPacket(inBuffer);
        }

        private void ClearLatchedFaults(int axis)
        {
            byte[] value = new byte[4];
            value[0] = 0x00;
            value[0] = 0x10;
            value[0] = 0x00;
            value[0] = 0x00;

            CopleyInterfacePacket packet = CopleyMotorCommands.CreatePacket(axis, CopleyOpcodes.setVariable, 0x00A1, value);
        }

        private void PrintCommandPacket(CopleyInterfacePacket packet, string prefix)
        {
            Console.WriteLine();
            Console.Write(prefix + ": ");

            Console.Write(packet.node.ToString("X2") + " ");
            Console.Write(packet.checksum.ToString("X2") + " ");
            Console.Write(packet.dataSize.ToString("X2") + " ");
            Console.Write(packet.opcode.ToString("X2") + " ");

            if (packet.data != null)
            {
                foreach (byte b in packet.data)
                {
                    Console.Write(b.ToString("X2") + " ");
                }
            }

            Console.WriteLine();
        }

        void MotorControllerInterface.InitialiseXMotor()
        {
            WritePacket(setTrajectoryModeAbsoluteX, responseTimeout);
            WritePacket(turnOnAmplifierX, responseTimeout);
            WritePacket(startHomingX, responseTimeout);
        }

        void MotorControllerInterface.InitialiseYMotor()
        {
            WritePacket(setTrajectoryModeAbsoluteY, responseTimeout);
            WritePacket(turnOnAmplifierY, responseTimeout);
            WritePacket(startHomingY, responseTimeout);
        }

        void MotorControllerInterface.DisableXMotor()
        {
            WritePacket(turnOffAmplifierX, responseTimeout);
        }

        void MotorControllerInterface.DisableYMotor()
        {
            WritePacket(turnOffAmplifierY, responseTimeout);
        }

        void MotorControllerInterface.MoveXMotor(int position)
        {
            byte[] positionArray = BitConverter.GetBytes(position);

            Array.Reverse(positionArray);

            CopleyInterfacePacket setPosition = CopleyMotorCommands.CreatePacket(xAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.positionCommand, positionArray);

            WritePacket(setTrajectoryModeAbsoluteX, responseTimeout);
            WritePacket(setPosition, responseTimeout);
            WritePacket(startMoveX, responseTimeout);
        }

        void MotorControllerInterface.MoveYMotor(int position)
        {
            byte[] positionArray = BitConverter.GetBytes(position);

            Array.Reverse(positionArray);

            CopleyInterfacePacket setPosition = CopleyMotorCommands.CreatePacket(yAxis, CopleyOpcodes.setVariable, CopleyVariableIdentifiers.positionCommand, positionArray);

            WritePacket(setTrajectoryModeAbsoluteY, responseTimeout);
            WritePacket(setPosition, responseTimeout);
            WritePacket(startMoveY, responseTimeout);
        }

        int MotorControllerInterface.GetXMotorPosition()
        {
            int pos = lastGoodXPosition;
            CopleyInterfacePacket response = WritePacket(readPositionX, responseTimeout);

            if (CopleyMotorCommands.ChecksumValid(response))
            {
                if (response.data != null)
                {
                    if (response.data.Length == 4)
                    {
                        Array.Reverse(response.data);
                        pos = BitConverter.ToInt32(response.data, 0);
                        lastGoodXPosition = pos;
                    }
                }
            }

            return pos;
        }

        int MotorControllerInterface.GetYMotorPosition()
        {
            int pos = lastGoodYPosition;
            CopleyInterfacePacket response = WritePacket(readPositionY, responseTimeout);

            if (CopleyMotorCommands.ChecksumValid(response))
            {
                if (response.data != null)
                {
                    if (response.data.Length == 4)
                    {
                        Array.Reverse(response.data);
                        pos = BitConverter.ToInt32(response.data, 0);
                        lastGoodYPosition = pos;
                    }
                }
            }

            return pos;
        }
    }

    public static class CopleyOpcodes
    {
        public const byte operatingMode = 0x07;
        public const byte getVariable = 0x0C;
        public const byte setVariable = 0x0D;
        public const byte trajectoryCommand = 0x11;
    }

    public static class CopleyVariableIdentifiers
    {
        public const byte latchedFaultRegister = 0xA1;
        public const byte trajectoryProfileMode = 0xC8;
        public const byte positionCommand = 0xCA;
        public const byte amplifierState = 0x24;
        public const byte startMove = 0x01;
        public const byte startHoming = 0x02;
        public const byte actualPosition = 0x17;
    }

    public struct CopleyInterfacePacket
    {
        public byte node;
        public byte checksum;
        public byte dataSize;
        public byte opcode;
        public byte[] data;
    }

    public static class CopleyMotorCommands
    {
        public const int commandPacketHeaderSize = 4;

        private const int variableIdentifierWordSize = 2;
        private const byte variableIdentifierWordByte0 = 0;
        private const byte variableIdentifierWordByte1 = 1;

        private const byte checksumResult = 0x5A;

        public static byte[] ToByteArray(CopleyInterfacePacket commandPacket)
        {
            int dataLength = 0;
            if (commandPacket.data != null)
            {
                dataLength = commandPacket.data.Length;
            }

            byte[] byteArray = new byte[commandPacketHeaderSize + dataLength];
            byteArray[0] = commandPacket.node;
            byteArray[1] = commandPacket.checksum;
            byteArray[2] = commandPacket.dataSize;
            byteArray[3] = commandPacket.opcode;

            if (commandPacket.data != null)
            {
                int dataIndex = commandPacketHeaderSize;
                foreach (byte b in commandPacket.data)
                {
                    byteArray[dataIndex] = b;
                    dataIndex++;
                }
            }

            return byteArray;
        }

        public static CopleyInterfacePacket ToCommandPacket(byte[] byteArray)
        {
            if (byteArray.Length < commandPacketHeaderSize)
            {
                return new CopleyInterfacePacket();
            }

            CopleyInterfacePacket commandPacket = new CopleyInterfacePacket();
            commandPacket.node = byteArray[0];
            commandPacket.checksum = byteArray[1];
            commandPacket.dataSize = byteArray[2];
            commandPacket.opcode = byteArray[3];

            int dataBytes = byteArray.Length - commandPacketHeaderSize;

            commandPacket.data = new byte[dataBytes];

            for (int i = 0; i < dataBytes; i++)
            {
                commandPacket.data[i] = byteArray[commandPacketHeaderSize + i];
            }

            return commandPacket;
        }

        public static CopleyInterfacePacket ToCommandPacket(List<byte> byteList)
        {
            return ToCommandPacket(byteList.ToArray());
        }

        //From the Copley Controls Binary Serial Interface document (AN112 v1.1):
        //"The checksum is calculated by performing an exclusive OR operation on every byte
        // of the command packet (including the header and checksum value). The result should
        // equal the hex constant 5A."
        public static bool ChecksumValid(CopleyInterfacePacket commandPacket)
        {
            byte result = 0;

            result = (byte)(result ^ commandPacket.node);
            result = (byte)(result ^ commandPacket.checksum);
            result = (byte)(result ^ commandPacket.dataSize);
            result = (byte)(result ^ commandPacket.opcode);

            if (commandPacket.data != null)
            {
                foreach (byte dataByte in commandPacket.data)
                {
                    result ^= dataByte;
                }
            }

            return result == checksumResult;
        }

        public static bool ChecksumValid(List<byte> commandPacket)
        {
            if (commandPacket == null)
            {
                return false;
            }

            byte result = 0;
            foreach (byte dataByte in commandPacket)
            {
                result ^= dataByte;
            }

            return result == checksumResult;
        }

        //Set the checksum byte value so that the overall
        //checksum of the command packet to equal '5A'.
        private static void SetChecksumByte(ref CopleyInterfacePacket commandPacket)
        {
            //Calculate the the checksum result of the command packet
            //minus the checksum byte.
            byte result = 0;

            result = (byte)(result ^ commandPacket.node);
            result = (byte)(result ^ commandPacket.dataSize);
            result = (byte)(result ^ commandPacket.opcode);

            foreach (byte dataByte in commandPacket.data)
            {
                result ^= dataByte;
            }

            //Calculate and set the checksum byte value.
            commandPacket.checksum = (byte)(result ^ checksumResult);
        }

        //Build and return a command packet given the opcode and command data.
        private static CopleyInterfacePacket BuildCommandPacket(byte opcode, byte[] data)
        {
            CopleyInterfacePacket commandPacket = new CopleyInterfacePacket();

            //Set the command header.
            commandPacket.node = 0x00;
            commandPacket.checksum = 0x00;
            commandPacket.dataSize = (byte)(data.Length / 2);
            commandPacket.opcode = opcode;

            //Copy in the data bytes.
            if (data == null)
            {
                data = new byte[0];
            }
            commandPacket.data = new byte[data.Length];
            Array.Copy(data, commandPacket.data, data.Length);

            //Set the checksum byte.
            SetChecksumByte(ref commandPacket);

            return commandPacket;
        }

        //Create a command packet.
        public static CopleyInterfacePacket CreatePacket(int axis, byte opcode, int variableIdentifier, byte[] value)
        {
            if (value == null)
            {
                value = new byte[0];
            }

            //The data consist of a 'variable identifier word' (2 bytes) plus the value to write.
            byte[] data = new byte[variableIdentifierWordSize + value.Length];

            //The 'variable identifier word' is as follows:
            //  Bits     Description
            //  0-8      Variable identifier number
            //  9-11     N/A
            //  12       Bank to write variable to (should always be zero)
            //  13-15    Axis number

            //Set bits 0-8.
            UInt16 variableIdentifierWord = (UInt16)variableIdentifier;

            //Set bits 13-15 (i.e. mask in the axis number)
            variableIdentifierWord |= (UInt16)(axis << 13);

            //Split the 'variable identifier word' into two separate bytes.
            data[variableIdentifierWordByte0] = (byte)((variableIdentifierWord & 0xFF00) >> 8);
            data[variableIdentifierWordByte1] = (byte)(variableIdentifierWord & 0x00FF);

            //Copy in the value to write.
            int valueIndex = variableIdentifierWordByte1 + 1;
            foreach (byte valueByte in value)
            {
                data[valueIndex] = valueByte;
                valueIndex++;
            }

            //Create the full command.
            return BuildCommandPacket(opcode, data);
        }

        public static CopleyInterfacePacket CreatePacket(byte opcode)
        {
            //Create the full command.
            byte[] data = new byte[0];
            return BuildCommandPacket(opcode, data);
        }

    }
}