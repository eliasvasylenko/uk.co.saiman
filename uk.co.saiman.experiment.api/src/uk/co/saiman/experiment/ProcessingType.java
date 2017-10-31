package uk.co.saiman.experiment;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;

public interface ProcessingType<S, T, U> extends ExperimentType<S, U> {
  @Override
  default boolean hasAutomaticExecution() {
    return true;
  }

  @Override
  default boolean isExecutionContextDependent() {
    return false;
  }

  @Override
  default U execute(ExecutionContext<S> context) {
    @SuppressWarnings("unchecked")
    T input = (T) context.node().getParent().get().getResult().get();
    return process(input);
  }

  U process(T input);

  @Override
  default boolean mayComeAfter(ExperimentNode<?, ?> parentNode) {
    return parentNode.getType().getResultType().getDataType().isAssignableTo(getInputType());
  }

  @Override
  default boolean mayComeBefore(
      ExperimentNode<?, ?> penultimateDescendantNode,
      ExperimentType<?, ?> descendantNodeType) {
    return true;
  }

  /**
   * @return the exact generic type of the input of this processing step
   */
  default TypeToken<T> getInputType() {
    return forType(getThisType())
        .resolveSupertype(ExperimentType.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }
}
