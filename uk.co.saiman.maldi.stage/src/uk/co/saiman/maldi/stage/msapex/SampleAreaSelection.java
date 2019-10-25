package uk.co.saiman.maldi.stage.msapex;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import uk.co.saiman.maldi.sampleplate.MaldiSampleArea;

public class SampleAreaSelection {
  private static final SampleAreaSelection EMPTY = new SampleAreaSelection(Set.of());

  private final Set<MaldiSampleArea> sampleAreas;

  private SampleAreaSelection(Set<MaldiSampleArea> sampleAreas) {
    this.sampleAreas = sampleAreas;
  }

  public static SampleAreaSelection empty() {
    return EMPTY;
  }

  public Stream<MaldiSampleArea> sampleAreas() {
    return sampleAreas.stream();
  }

  public SampleAreaSelection with(MaldiSampleArea... sampleAreas) {
    return with(asList(sampleAreas));
  }

  public SampleAreaSelection with(Collection<? extends MaldiSampleArea> sampleAreas) {
    var newSampleAreas = new HashSet<>(this.sampleAreas);
    newSampleAreas.addAll(sampleAreas);
    return new SampleAreaSelection(newSampleAreas);
  }

  public SampleAreaSelection without(MaldiSampleArea... sampleAreas) {
    return without(asList(sampleAreas));
  }

  public SampleAreaSelection without(Collection<? extends MaldiSampleArea> sampleAreas) {
    var newSampleAreas = new HashSet<>(this.sampleAreas);
    newSampleAreas.removeAll(sampleAreas);
    return new SampleAreaSelection(newSampleAreas);
  }

  @Override
  public String toString() {
    return sampleAreas.toString();
  }
}
