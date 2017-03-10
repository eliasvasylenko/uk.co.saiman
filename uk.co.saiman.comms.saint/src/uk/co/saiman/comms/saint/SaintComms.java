package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.CommandSet;

public interface SaintComms extends CommandSet<SaintCommandId> {
	String ID = "SAINT Comms";
	
	InOutBlock<StatusLedBits> led();

	InOutBlock<VacuumBits> vacuum();

	OutBlock<HighVoltageBits> highVoltage();
}
