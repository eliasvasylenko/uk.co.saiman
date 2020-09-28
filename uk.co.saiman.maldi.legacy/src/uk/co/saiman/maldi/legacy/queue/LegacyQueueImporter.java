/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.maldi.legacy.queue.
 *
 * uk.co.saiman.maldi.legacy.queue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.legacy.queue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import uk.co.saiman.maldi.legacy.settings.MaldiLegacySettings;
import uk.co.saiman.maldi.sample.MaldiSampleAreaExecutor;
import uk.co.saiman.maldi.sample.MaldiSamplePlateExecutor;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor;

public class LegacyQueueImporter {
  private static final String EXPERIMENT = "experiment";
  private static final String E_NAME = "EName";
  private static final String E_GROUP = "EGroup"; // tag outpu
  private static final String E_ROOT = "ERoot"; // export to here
  private static final String E_SAMPLE_ID = "ESampleId";
  private static final String E_USERNAME = "EUsername"; // ignore
  private static final String E_EQUIPMENT_ID = "EEquipmentId"; // ignore
  private static final String E_WELL_NAME = "EWellName";
  private static final String E_SLIDE_NAME = "ESlideName";

  private static final String E_MALDI_ION_SOURCE = "EMALDIIonSource";
  private static final String E_MALDI_TOF_ANALYSER = "EMALDIToFAnalyser";
  private static final String E_MALDI_DATA_COLLECTION = "EMALDIDataCollection"; // digitisation rate -- ignored
  private static final String E_MALDI_OPTIMISATION_PARAMETERS = "EMALDIOptimisationParameters";
  private static final String E_MALDI_INTERNAL_CALIBRATION = "EMALDIInternalCalibration"; // TODO
  private static final String E_MALDI_MASS_SPECTRUM_PEAK_DETECTION_SETUP = "EMALDIMassSpectrumPeakDetectionSetup"; // add
                                                                                                                   // processing
                                                                                                                   // snippet
  private static final String E_MALDI_PROCESSING_METHOD = "EMALDIProcessingMethod"; // add processing snippet

  private final MaldiSamplePlateExecutor samplePlate;
  private final MaldiSampleAreaExecutor sampleArea;
  private final MaldiSpectrumExecutor spectrum;

  private final MaldiLegacySettings legacySettings;

  public LegacyQueueImporter(
      MaldiSamplePlateExecutor samplePlate,
      MaldiSampleAreaExecutor sampleArea,
      MaldiSpectrumExecutor spectrum,
      MaldiLegacySettings legacySettings) {
    this.samplePlate = samplePlate;
    this.sampleArea = sampleArea;
    this.spectrum = spectrum;

    this.legacySettings = legacySettings;
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
    var slideName = getTextContent(queue, E_SLIDE_NAME, "96 Well Plate with DM");

    var experiments = queue.getElementsByTagName(EXPERIMENT);

    var wells = IntStream
        .range(0, experiments.getLength())
        .mapToObj(i -> importSpectrum(i, (Element) experiments.item(i)))
        .collect(toList());

    var plate = StepDefinition.define(fromName(slideName), samplePlate).withSubsteps(wells);

    return ExperimentDefinition.define(name).withSubstep(plate);
  }

  StepDefinition importSpectrum(int index, Element element) {
    var sampleAreaId = getTextContent(element, E_NAME, null);
    var spectrumId = getTextContent(
        element,
        E_SAMPLE_ID,
        getTextContent(element, E_WELL_NAME, Integer.toString(index)));

    var ionSource = legacySettings.lookupIonSource(getTextContent(element, E_MALDI_ION_SOURCE, null));
    var tofAnalyser = legacySettings.lookupTofAnalyser(getTextContent(element, E_MALDI_TOF_ANALYSER, null));
    var optimisation = legacySettings
        .lookupOptimisation(getTextContent(element, E_MALDI_OPTIMISATION_PARAMETERS, null));
    var internalCalibration = legacySettings
        .lookupInternalCalibration(getTextContent(element, E_MALDI_INTERNAL_CALIBRATION, null));
    var peakDetection = legacySettings
        .lookupPeakDetection(getTextContent(element, E_MALDI_MASS_SPECTRUM_PEAK_DETECTION_SETUP, null));
    var processing = legacySettings.lookupProcessing(getTextContent(element, E_MALDI_PROCESSING_METHOD, null));

    return StepDefinition
        .define(fromName(sampleAreaId), sampleArea)
        .withSubstep(StepDefinition.define(fromName(spectrumId), spectrum));
  }

  String getTextContent(Element element, String tag, String def) {
    var elements = element.getElementsByTagName(tag);
    return IntStream
        .range(0, elements.getLength())
        .mapToObj(elements::item)
        .map(Node::getTextContent)
        .filter(s -> !s.isBlank())
        .reduce((a, b) -> {
          if (!a.equals(b))
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
