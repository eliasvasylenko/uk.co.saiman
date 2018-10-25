/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.msapex.editor.
 *
 * uk.co.saiman.msapex.editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.editor.impl;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.eclipse.e4.ui.workbench.UIEvents.Context.TOPIC_CONTEXT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.ELEMENT;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.NEW_VALUE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTags.TYPE;
import static org.eclipse.e4.ui.workbench.UIEvents.EventTypes.SET;

import java.nio.file.Path;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.msapex.editor.EditorService;

public class FileEditorAddon {
  /**
   * The persisted file name of a resource for an active editor.
   */
  public static final String EDITOR_FILE_NAME = "uk.co.saiman.msapex.editor.file.name";
  /**
   * The persistent data key for the Unix-style glob pattern which determines
   * applicability of an editor to a resource path.
   */
  public static final String EDITOR_FILE_PATH_PATTERN = "uk.co.saiman.msapex.editor.file.path.pattern";

  @Inject
  private EModelService modelService;
  @Inject
  private EditorService editorService;
  @Inject
  private MApplication application;
  @Inject
  private Log log;

  private Map<String, FileEditorProvider> providers;

  @PostConstruct
  synchronized void create() {
    providers = application
        .getSnippets()
        .stream()
        .filter(this::isFileEditor)
        .map(snippet -> new FileEditorProvider(application, (MPart) snippet))
        .collect(toMap(FileEditorProvider::getId, identity()));

    modelService
        .findElements(application, MPart.class, EModelService.ANYWHERE, part -> isFileEditor(part))
        .forEach(this::addEditor);

    providers.values().forEach(editorService::registerProvider);
  }

  @PreDestroy
  void destroy() {
    providers.values().forEach(editorService::unregisterProvider);
  }

  private void addEditor(MPart part) {
    FileEditorProvider provider = providers.get(part.getElementId());
    if (provider != null) {
      provider.addEditor(part);
    }
  }

  private boolean isFileEditor(MApplicationElement model) {
    return model.getPersistedState().containsKey(EDITOR_FILE_PATH_PATTERN);
  }

  /*
   * Part lifecycle in the application model
   */

  @Inject
  @Optional
  private void partOpenListener(@UIEventTopic(TOPIC_CONTEXT) Event event) {
    try {
      Object value = event.getProperty(NEW_VALUE);
      Object element = event.getProperty(ELEMENT);

      if (element instanceof MPart
          && value instanceof IEclipseContext
          && SET.equals(event.getProperty(TYPE))) {
        IEclipseContext context = (IEclipseContext) value;
        MPart part = (MPart) element;

        if (isFileEditor(part)) {
          context.set(Path.class, (Path) part.getTransientData().get(EDITOR_FILE_NAME));
        }
      }
    } catch (Exception e) {
      log.log(Level.ERROR, e);
    }
  }
}
