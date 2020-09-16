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
package uk.co.saiman.maldi.stage.msapex;

import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.core.di.Service;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;

import uk.co.saiman.experiment.sampleplate.SamplePlate;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlateIndex;

public class MaldiStageAddon {
  private static final String SELECTED_PLATE_KEY = "selected.plate";

  @Inject
  @Service
  private Log log;

  @Inject
  private MAddon addon;
  private String defaultPlate;

  private SamplePlatePresentationService presentationService;

  @Inject
  private IEclipseContext context;

  @PostConstruct
  void initialize(
      @OSGiBundle BundleContext bundleContext,
      MApplication application,
      EModelService models) {

    context.declareModifiable(SamplePlate.class);
    context.declareModifiable(MaldiSamplePlate.class);
    context.declareModifiable(SamplePlatePresenter.class);

    context.declareModifiable(SampleAreaSelection.class);

    presentationService = new SamplePlatePresentationService(application, models);

    context.set(SamplePlatePresentationService.class, presentationService);

    defaultPlate = addon.getPersistedState().get(SELECTED_PLATE_KEY);
    setPlateIndex(context.get(MaldiSamplePlateIndex.class));

    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        setPlateFromContext(context);

        return true;
      }
    });
  }

  private void setPlateFromContext(IEclipseContext context) {
    var plate = context.get(MaldiSamplePlate.class);
    context.modify(SamplePlate.class, plate);

    var platePresenter = presentationService.getPresenter(plate).orElse(null);
    context.modify(SamplePlatePresenter.class, platePresenter);

    var plateIndex = context.get(MaldiSamplePlateIndex.class);
    plateIndex.getId(plate).ifPresent(id -> {
      defaultPlate = id;
      addon.getPersistedState().put(SELECTED_PLATE_KEY, defaultPlate);
    });
  }

  @Inject
  @Optional
  private void windowOpenListener(@UIEventTopic(TOPIC_CONTEXT) Event event) {
    try {
      Object value = event.getProperty(NEW_VALUE);
      Object element = event.getProperty(ELEMENT);

      if (element instanceof MWindow
          && value instanceof IEclipseContext
          && SET.equals(event.getProperty(TYPE))) {

        setPlateFromContext(this.context);
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }

  @Inject
  void setPlateIndex(@Optional MaldiSamplePlateIndex plateIndex) {
    context.set(MaldiSamplePlateIndex.class, plateIndex);

    MaldiSamplePlate currentPlate = context.get(MaldiSamplePlate.class);
    MaldiSamplePlate selection = currentPlate;

    if (plateIndex != null && currentPlate == null) {
      var findDefault = ((defaultPlate == null)
          ? plateIndex.getSamplePlates().findFirst()
          : plateIndex.getSamplePlate(defaultPlate));
      selection = findDefault.orElse(null);

    } else if (plateIndex == null || plateIndex.getId(currentPlate).isEmpty()) {
      selection = null;
    }

    if (currentPlate != selection) {
      if (selection != null) {
        context.modify(MaldiSamplePlate.class, selection);
      } else {
        context.remove(MaldiSamplePlate.class);
      }
    }
  }
}
