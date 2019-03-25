package uk.co.saiman.experiment.procedure;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.saiman.experiment.path.Dependency;
import uk.co.saiman.experiment.product.Product;

public class IndirectRequirement<T extends Product> {
  private final ProductRequirement<T> requirement;
  private final Function<RequirementResolutionContext, Stream<? extends Dependency<? extends T, ?>>> dependencies;

  public IndirectRequirement(
      ProductRequirement<T> requirement,
      Function<RequirementResolutionContext, Stream<? extends Dependency<? extends T, ?>>> dependencies) {
    this.requirement = requirement;
    this.dependencies = dependencies;
  }

  public Requirement<T> requirement() {
    return requirement;
  }

  public Stream<? extends Dependency<? extends T, ?>> dependencies(
      RequirementResolutionContext context) {
    return dependencies.apply(context);
  }

  @SuppressWarnings("unchecked")
  public <U extends Product> Optional<IndirectRequirement<U>> matching(Requirement<U> requirement) {
    return this.requirement.equals(requirement)
        ? Optional.of((IndirectRequirement<U>) this)
        : Optional.empty();
  }
}
