package uk.co.saiman.experiment;

import static uk.co.saiman.reflection.token.TypeToken.forType;

import java.lang.reflect.Type;

import uk.co.saiman.reflection.token.TypeArgument;
import uk.co.saiman.reflection.token.TypeParameter;
import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * An observation can be made during an {@link ExperimentStep experiment step}
 * to produce a {@link Result result}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T> the type of the result we wish to find
 */
public abstract class Observation<T> {
  private final String id;

  public Observation(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }

  public Type getThisType() {
    return getClass();
  }

  public TypeToken<T> getResultType() {
    return forType(getThisType())
        .resolveSupertype(Procedure.class)
        .resolveTypeArgument(new TypeParameter<T>() {})
        .getTypeToken();
  }

  public TypeToken<Observation<T>> getThisTypeToken() {
    return new TypeToken<Observation<T>>() {}
        .withTypeArguments(new TypeArgument<T>(getResultType()) {});
  }

  public TypedReference<Observation<T>> asTypedObject() {
    return TypedReference.typedObject(getThisTypeToken(), this);
  }
}
