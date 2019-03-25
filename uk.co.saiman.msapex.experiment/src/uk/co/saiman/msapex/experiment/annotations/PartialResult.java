package uk.co.saiman.msapex.experiment.annotations;

public @interface PartialResult {
  String observationId() default "";
}
