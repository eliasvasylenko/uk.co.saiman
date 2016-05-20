package uk.co.saiman.experiment.sample;

/**
 * A base interface for sample source configuration. To be extended by e.g. an
 * X-Y stage configuration, or an inlet valve configuration.
 * 
 * @author Elias N Vasylenko
 */
public interface StageConfiguration extends SampleConfiguration {
	double getX();
}
