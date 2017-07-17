package uk.co.saiman.comms.saint;

public interface SaintController {
  Value<LEDStatus> statusLED();

  Value<VacuumControl> vacuum();

  ValueRequest<HighVoltageStatus> highVoltage();

  Value<MotorStatus> motorStatus();

  Value<VacuumReadback> vacuumReadback();

  Value<HighVoltageReadback> highVoltageReadback();

  ValueRequest<I2C> highVoltageDAC1();

  ValueRequest<I2C> highVoltageDAC2();

  ValueRequest<I2C> highVoltageDAC3();

  ValueRequest<I2C> highVoltageDAC4();

  ValueRequest<I2C> cmosRef();

  ValueRequest<I2C> laserDetectRef();

  ValueReadback<ADC> piraniReadback();

  ValueReadback<ADC> magnetronReadback();

  ValueReadback<ADC> spareMon1();

  ValueReadback<ADC> spareMon2();

  ValueReadback<ADC> spareMon3();

  ValueReadback<ADC> spareMon4();

  ValueReadback<ADC> currentReadback1();

  ValueReadback<ADC> currentReadback2();

  ValueReadback<ADC> currentReadback3();

  ValueReadback<ADC> currentReadback4();

  ValueReadback<ADC> voltageReadback1();

  ValueReadback<ADC> voltageReadback2();

  ValueReadback<ADC> voltageReadback3();

  ValueReadback<ADC> voltageReadback4();

  ValueRequest<TurboControl> turboControl();

  ValueReadback<TurboReadbacks> turboReadbacks();
}
