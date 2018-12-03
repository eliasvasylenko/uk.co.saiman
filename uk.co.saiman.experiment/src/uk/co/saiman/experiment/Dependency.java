package uk.co.saiman.experiment;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.Type;

import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * An input to an experiment procedure should be wired up to an observation made
 * by a preceding procedure.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the result we wish to find
 */
public abstract class Dependency<T> {
  public Type getThisType() {
    return getClass();
  }

  public TypeToken<T> getResultType() {
    return forType(getThisType())
        .resolveSupertype(Procedure.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }

  public TypeToken<Dependency<T>> getThisTypeToken() {
    return new TypeToken<Dependency<T>>() {}
        .withTypeArguments(new TypeArgument<T>(getResultType()) {});
  }

  public TypedReference<Dependency<T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }

  public boolean isSatisfiedBy(Observation<?> observation) {
    return observation.getResultType().isAssignableTo(getResultType());
  }
}
