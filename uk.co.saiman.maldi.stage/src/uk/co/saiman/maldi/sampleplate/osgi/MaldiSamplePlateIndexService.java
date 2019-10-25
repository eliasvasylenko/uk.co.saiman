package uk.co.saiman.maldi.sampleplate.osgi;

import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.environment.osgi.SharedResource;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlateIndex;

@SharedResource
@Component
public class MaldiSamplePlateIndexService implements MaldiSamplePlateIndex {
  private final BundleContext context;
  private final Set<ServiceReference<MaldiSamplePlate>> references;

  private final Map<String, MaldiSamplePlate> platesById = new HashMap<>();
  private final Map<MaldiSamplePlate, String> idsByPlate = new HashMap<>();

  @Activate
  public MaldiSamplePlateIndexService(
      BundleContext context,
      @Reference(policyOption = GREEDY) List<ServiceReference<MaldiSamplePlate>> samplePlates) {
    this.context = context;
    this.references = new HashSet<>();

    for (var samplePlate : samplePlates) {
      var id = samplePlate.getProperty(SAMPLE_PLATE_ID).toString();
      if (id != null) {
        references.add(samplePlate);
        var plate = context.getService(samplePlate);

        platesById.put(id, plate);
        idsByPlate.put(plate, id);
      }
    }
  }

  @Deactivate
  public void deactivate() {
    references.forEach(context::ungetService);
  }

  @Override
  public Stream<MaldiSamplePlate> getSamplePlates() {
    return platesById.values().stream();
  }

  @Override
  public Optional<MaldiSamplePlate> getSamplePlate(String id) {
    return Optional.ofNullable(platesById.get(id));
  }

  @Override
  public Optional<String> getId(MaldiSamplePlate samplePlate) {
    return Optional.ofNullable(idsByPlate.get(samplePlate));
  }
}
