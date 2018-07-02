package uk.co.saiman.bytes.impl;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.bytes.conversion.ByteConversionAnnotations.collectAnnotations;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.bytes.BitArray;
import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.reflection.token.AnnotatedTypeToken;

public class CompositeByteConverter<T> implements ByteConverter<T> {
  private final ByteConverterServiceImpl service;
  private final AnnotatedTypeToken<T> type;
  private final ByteConversionAnnotations annotations;

  private final Map<ByteConverterProvider, ByteConverter<T>> components;

  CompositeByteConverter(ByteConverterServiceImpl service, AnnotatedTypeToken<T> type) {
    this.service = service;
    this.type = type;
    this.components = new HashMap<>();
    this.annotations = findAnnotations();
  }

  @Override
  public T toObject(BitArray bits) {
    return getConverter().toObject(bits);
  }

  @Override
  public BitArray toBits(T object) {
    return getConverter().toBits(object);
  }

  private Stream<ByteConverterProvider> getProviders() {
    return service.getProviders();
  }

  private ByteConverter<T> getConverter() {
    List<ByteConverterProvider> providers = getProviders().collect(toList());

    components.keySet().retainAll(providers);

    return providers
        .stream()
        .map(this::getConverter)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find converter for type " + type));
  }

  private ByteConverter<T> getConverter(ByteConverterProvider provider) {
    return components.computeIfAbsent(provider, this::createConverter);
  }

  @SuppressWarnings("unchecked")
  private ByteConverter<T> createConverter(ByteConverterProvider provider) {
    if (!annotations
        .getAll()
        .map(Annotation::annotationType)
        .allMatch(provider::supportsAnnotation)) {
      return null;
    }

    ByteConverter<?> converter = provider
        .getConverter(type.getAnnotatedType(), annotations, service);
    return (ByteConverter<T>) converter;
  }

  private ByteConversionAnnotations findAnnotations() {
    return collectAnnotations(type.getAnnotatedType())
        .and(collectAnnotations(type.getErasedType()));
  }
}
