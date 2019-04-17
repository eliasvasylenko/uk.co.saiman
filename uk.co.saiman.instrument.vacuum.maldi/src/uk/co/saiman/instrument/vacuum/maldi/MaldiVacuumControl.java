package uk.co.saiman.instrument.vacuum.maldi;

import java.util.concurrent.TimeUnit;

import uk.co.saiman.instrument.DeviceControlImpl;
import uk.co.saiman.instrument.vacuum.VacuumControl;

public class MaldiVacuumControl extends DeviceControlImpl<MaldiVacuumDevice>
    implements VacuumControl {
  public MaldiVacuumControl(MaldiVacuumDevice device, long timeout, TimeUnit unit) {
    super(device, timeout, unit);
  }
}
