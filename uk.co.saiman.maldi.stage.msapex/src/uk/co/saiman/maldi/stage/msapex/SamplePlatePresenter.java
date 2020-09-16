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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.fx.ui.workbench.renderers.base.widget.WWidget;

import javafx.scene.Node;
import uk.co.saiman.experiment.sampleplate.SamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlate;
import uk.co.saiman.maldi.sampleplate.MaldiSamplePlateIndex;

public class SamplePlatePresenter {
  private final String id;
  private final MWindow window;
  private final MPart part;

  private IEclipseContext context;
  private Node widget;

  private SamplePlatePresenter(String id, MWindow window, MPart part) {
    this.id = id;
    this.window = window;
    this.part = part;
  }

  public static Optional<SamplePlatePresenter> createPresenter(MWindow window, MUIElement element) {
    if (!(element instanceof MPart)) {
      return java.util.Optional.empty();
    }
    var part = (MPart) element;

    var id = element.getPersistedState().get(MaldiSamplePlateIndex.SAMPLE_PLATE_ID);
    if (id == null) {
      return java.util.Optional.empty();
    }

    return java.util.Optional.of(new SamplePlatePresenter(id, window, part));
  }

  public String getPlateId() {
    return id;
  }

  public Optional<MaldiSamplePlate> getPlate() {
    return Optional.ofNullable(getContext()).map(c -> c.get(MaldiSamplePlate.class));
  }

  public String getLocalizedLabel() {
    return part.getLocalizedLabel();
  }

  public String getIconURI() {
    return part.getIconURI();
  }

  public void present() {
    if (getContext() != null) {
      var context = window.getContext();

      var plate = getPlate().orElse(null);
      context.modify(MaldiSamplePlate.class, plate);
      context.modify(SamplePlate.class, plate);

      context.modify(SamplePlatePresenter.class, this);
    }
  }

  MPart getPart() {
    return part;
  }

  synchronized IEclipseContext getContext() {
    if (context == null && window.getContext() != null) {
      context = window.getContext().createChild();

      context.set(SamplePlatePresenter.class, this);

      Set<Class<?>> previousPlateClasses = new HashSet<>();
      context.runAndTrack(new RunAndTrack() {
        @Override
        public boolean changed(IEclipseContext context) {
          synchronized (SamplePlatePresenter.this) {
            var index = context.get(MaldiSamplePlateIndex.class);
            var plate = index.getSamplePlate(id).orElse(null);

            context.set(MaldiSamplePlate.class, plate);
            context.set(SamplePlate.class, plate);

            for (var previousPlateClass : previousPlateClasses) {
              context.remove(previousPlateClass);
            }
            previousPlateClasses.clear();
            if (plate != null) {
              Class<?> plateClass = plate.getClass();
              do {
                previousPlateClasses.add(plateClass);
                context.set(plateClass.getName(), plate);
                plateClass = plateClass.getSuperclass();
              } while (plateClass != Object.class);
            }

            return true;
          }
        }
      });
    }
    return context;
  }

  synchronized Node getWidget() {
    if (widget == null && getContext() != null) {
      part.setToBeRendered(true);
      part.setVisible(false);
      window.getSharedElements().remove(part);
      window.getSharedElements().add(0, part);

      context.get(IPresentationEngine.class).createGui(part, null, context);

      widget = (Node) ((WWidget<?>) part.getWidget()).getWidget();
    }
    return widget;
  }
}
