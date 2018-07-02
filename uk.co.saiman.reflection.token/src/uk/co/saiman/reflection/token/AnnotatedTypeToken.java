package uk.co.saiman.reflection.token;

import java.lang.reflect.AnnotatedType;

public class AnnotatedTypeToken<T> extends TypeToken<T> {
  private final AnnotatedType annotatedType;

  protected AnnotatedTypeToken() {
    /*
     * TODO this gets called twice, but since we can't call it before invoking
     * 'super' this is difficult to avoid.
     */
    this.annotatedType = resolveSuperclassParameter();
  }

  protected AnnotatedTypeToken(AnnotatedType annotatedType) {
    super(annotatedType.getType());
    this.annotatedType = annotatedType;
  }

  public AnnotatedType getAnnotatedType() {
    return annotatedType;
  }

  /**
   * Create a TypeToken for an arbitrary type, preserving wildcards where
   * possible.
   * 
   * @param type
   *          the requested type
   * @return a TypeToken over the requested type
   */
  public static AnnotatedTypeToken<?> forType(AnnotatedType type) {
    return new AnnotatedTypeToken<>(type);
  }
}
