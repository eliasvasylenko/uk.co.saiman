package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.ExperimentPath;
import uk.co.saiman.experiment.product.Product;

public class Requirements {
  private final ProductRequirement<?> requirement;
  private final Function<RequirementResolutionContext, Stream<? extends ExperimentPath<?>>> dependencies;

  public Requirements(
      ProductRequirement<?> requirement,
      Function<RequirementResolutionContext, Stream<? extends ExperimentPath<?>>> dependencies) {
    this.requirement = requirement;
    this.dependencies = dependencies;
  }

  public ProductRequirement<?> requirement() {
    return requirement;
  }

  public Stream<? extends ExperimentPath<?>> dependencies(RequirementResolutionContext context) {
    return dependencies.apply(context);
  }

  public <U extends Product> Optional<Requirements> matching(Requirement<U> requirement) {
    return this.requirement.equals(requirement)
        ? Optional.of((Requirements) this)
        : Optional.empty();
  }
}
