package uk.co.saiman.experiment;

/**
 * The context of an experiment execution, providing information about the
 * current state, and enabling modification of that state.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the executing node
 */
public interface ExperimentExecutionContext<T> {
	/**
	 * @return the currently executing experiment node
	 */
	ExperimentNode<?, T> node();

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
	public <U> ExperimentResult<U> setResult(ExperimentResultType<U> resultType, U resultData);
}
