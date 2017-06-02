package uk.co.saiman.experiment;

import java.nio.file.Path;

public interface ExperimentResultManager {
	/**
	 * Experiment data root directories are defined hierarchically from the
	 * {@link ExperimentWorkspace#getWorkspaceDataPath() workspace path}.
	 * 
	 * @return the data root of the experiment
	 */
	Path dataPath();

	/**
	 * @param resultType
	 *          the type of result
	 * @return the result object now registered to the executing node
	 */
	public <U> ExperimentResult<U> get(ExperimentResultType<U> resultType);

	/**
	 * This method provides a target for the submission of results during
	 * execution of an experiment node.
	 * 
	 * @param resultType
	 *          the type of result
	 * @param resultData
	 *          the result
	 * @return the result object now registered to the executing node
	 */
	public <U> ExperimentResult<U> set(ExperimentResultType<U> resultType, U resultData);
}
