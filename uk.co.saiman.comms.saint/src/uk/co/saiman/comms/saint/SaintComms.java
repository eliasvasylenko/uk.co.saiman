package uk.co.saiman.comms.saint;

import uk.co.saiman.comms.CommandSet;
import uk.co.saiman.comms.NamedBits;
import uk.co.saiman.comms.NumberedBits;

public interface SaintComms extends CommandSet<SaintCommandId> {
	String ID = "SAINT Comms";

	InOutBlock<NumberedBits> led();

	InOutBlock<NamedBits<VacuumBit>> vacuum();

	OutBlock<NamedBits<HighVoltageBit>> highVoltage();
}
