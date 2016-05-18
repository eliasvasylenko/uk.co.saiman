package uk.co.saiman.experiment;

/**
 * General configuration interface for experiment root nodes, as created via
 * {@link ExperimentWorkspace#addRootExperiment(String)}
 * 
 * @author Elias N Vasylenko
 */
public interface ExperimentConfiguration {
	/**
	 * @return the name of the experiment
	 */
	String getName();

	/**
	 * @param name
	 *          the new name for the experiment
	 */
	void setName(String name);

	/**
	 * @return the notes of the experiment
	 */
	String getNotes();

	/**
	 * @param notes
	 *          the new notes for the experiment
	 */
	void setNotes(String notes);

	/**
	 * Set the name and notes to match those of the given configuration
	 * 
	 * @param configuration
	 *          the configuration to apply
	 */
	default void set(ExperimentConfiguration configuration) {
		setName(configuration.getName());
		setNotes(configuration.getNotes());
	}
}
