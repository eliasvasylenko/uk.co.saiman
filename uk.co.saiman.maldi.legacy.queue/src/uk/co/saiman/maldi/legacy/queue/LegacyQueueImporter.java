package uk.co.saiman.maldi.legacy.queue;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.READ;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.experiment.declaration.ExperimentId.fromName;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.experiment.definition.StepDefinition;
import uk.co.saiman.maldi.sample.MaldiSampleAreaExecutor;
import uk.co.saiman.maldi.sample.MaldiSamplePlateExecutor;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor;

public class LegacyQueueImporter {
  private final MaldiSamplePlateExecutor samplePlate;
  private final MaldiSampleAreaExecutor sampleArea;
  private final MaldiSpectrumExecutor spectrum;

  public LegacyQueueImporter(
      MaldiSamplePlateExecutor samplePlate,
      MaldiSampleAreaExecutor sampleArea,
      MaldiSpectrumExecutor spectrum) {
    this.samplePlate = samplePlate;
    this.sampleArea = sampleArea;
    this.spectrum = spectrum;
  }

  public ExperimentDefinition importLegacyQueue(ExperimentId name, Path queueFile) {
    try {
      var documentBuilderFactory = DocumentBuilderFactory.newInstance();
      var documentBuilder = documentBuilderFactory.newDocumentBuilder();
      var document = documentBuilder.parse(newInputStream(queueFile, READ));
      document.getDocumentElement().normalize();

      System.out.println(document.getDocumentElement());

      return importExperiment(name, document.getDocumentElement());

    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new LegacyQueueImportException("Legacy queue failed to load and parse", e);
    }
  }

  ExperimentDefinition importExperiment(ExperimentId name, Element queue) {
    var slideName = getTextContent(queue, "ESlideName", "96 Well Plate with DM");

    var experiments = queue.getElementsByTagName("experiment");

    var wells = IntStream
        .range(0, experiments.getLength())
        .mapToObj(i -> importSpectrum(i, (Element) experiments.item(i)))
        .collect(toList());

    var plate = StepDefinition.define(fromName(slideName), samplePlate).withSubsteps(wells);

    return ExperimentDefinition.define(name).withSubstep(plate);
  }

  StepDefinition importSpectrum(int index, Element element) {
    var wellName = getTextContent(element, "EWellName", null);
    var spectrumName = getTextContent(element, "EName", null);

    return StepDefinition
        .define(fromName(index + "-" + wellName), sampleArea)
        .withSubstep(StepDefinition.define(fromName(spectrumName), spectrum));
  }

  String getTextContent(Element element, String tag, String def) {
    var elements = element.getElementsByTagName(tag);
    return IntStream
        .range(0, elements.getLength())
        .mapToObj(elements::item)
        .map(Node::getTextContent)
        .reduce((a, b) -> {
          if (!a.isBlank() && !b.isBlank() && !a.equals(b))
            throw new LegacyQueueImportException(
                "Legacy queue must not declate multiple `" + tag + "` in `" + element.getTagName() + "`");
          return a;
        })
        .or(() -> Optional.ofNullable(def))
        .orElseThrow(
            () -> new LegacyQueueImportException(
                "Legacy queue must declare `" + tag + "` in `" + element.getTagName() + "`"));
  }
}
