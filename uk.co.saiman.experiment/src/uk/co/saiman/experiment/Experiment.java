package uk.co.saiman.experiment;

public interface Experiment {
	public enum ExperimentLifecycleState {
		/**
		 * 
		 */
		CONFIGURE,
		/**
		 * Move stage into position, etc.
		 */
		PREPARE,
		/**
		 * Optimise laser, acquire from tdc, etc.
		 */
		PROCESS,
		/**
		 * Finalise file, etc.
		 */
		COMPLETE
	}
}
