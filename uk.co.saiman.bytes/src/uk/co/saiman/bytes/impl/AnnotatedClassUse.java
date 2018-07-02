package uk.co.saiman.bytes.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

class AnnotatedClassUse implements AnnotatedType {
  private final Class<?> type;

  public AnnotatedClassUse(Class<?> type) {
    this.type = type;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return null;
  }

  @Override
  public Annotation[] getAnnotations() {
    return new Annotation[0];
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return new Annotation[0];
  }

  @Override
  public Type getType() {
    return type;
  }
}
