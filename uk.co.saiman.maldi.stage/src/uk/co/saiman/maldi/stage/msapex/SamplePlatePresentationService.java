package uk.co.saiman.maldi.stage.msapex;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.eclipse.e4.ui.workbench.modeling.EModelService.ANYWHERE;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;

public class SamplePlatePresentationService {
  private final Map<String, SamplePlatePresenter> presenters;

  SamplePlatePresentationService(MApplication application, EModelService models) {
    this.presenters = models
        .findElements(application, MWindow.class, ANYWHERE, e -> true)
        .stream()
        .filter(MWindow.class::isInstance)
        .map(MWindow.class::cast)
        .flatMap(window -> createPresenters(window))
        .collect(toMap(SamplePlatePresenter::getPlateId, identity(), (a, b) -> b));
  }

  static Stream<SamplePlatePresenter> createPresenters(MWindow window) {
    return window
        .getSharedElements()
        .stream()
        .flatMap(element -> SamplePlatePresenter.createPresenter(window, element).stream());
  }

  public Stream<SamplePlatePresenter> getPresenters() {
    return presenters.values().stream();
  }

  public Optional<SamplePlatePresenter> getPresenter(String plateId) {
    return Optional.ofNullable(presenters.get(plateId));
  }

  public Optional<SamplePlatePresenter> getPresenter(MaldiSamplePlate samplePlate) {
    return Optional
        .ofNullable(samplePlate)
        .flatMap(
            plate -> presenters
                .values()
                .stream()
                .filter(p -> p.getPlate().filter(plate::equals).isPresent())
                .findFirst());
  }
}
