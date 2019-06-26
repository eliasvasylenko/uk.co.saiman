package uk.co.saiman.instrument.sample;

/**
 * The device is ready for analysis, but no specific analysis location has been
 * requested. The {@link SampleDevice#samplePosition() location} of the device
 * may not be valid.
 */
public class Ready<T> extends RequestedSampleState<T> {

}