/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
