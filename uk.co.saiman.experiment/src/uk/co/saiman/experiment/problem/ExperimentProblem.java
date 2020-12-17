package uk.co.saiman.experiment.problem;

import uk.co.saiman.experiment.design.ExperimentDesign;
import uk.co.saiman.experiment.environment.Environment;

/**
 * A marker class to give a user-facing description of a problem with an
 * experiment.
 * 
 * Typically a problem instance will correspond to an exception thrown in the
 * process of {@link ExperimentDesign#materialize() materializing shared
 * methods} or {@link ExperimentDesign#implementProcedure(Environment)
 * implementing a procedure}. But there are some minor problems with this
 * motivating a higher-level representation of these failures.
 * <ul>
 * <li>Exceptions are not suitable to be held beyond the catching call
 * stack.</li>
 * <li>It is difficult to present exceptions to users in a friendly and
 * consistent way when exceptions have to be caught and processed in the UI
 * layer at every use-site of the API.</li>
 * <li>Exceptions might be too low-level for consumers of the API to understand,
 * this abstraction allows us to present a simple (and finite!) semantic model
 * of error state.</li>
 * <ul>
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentProblem {}
