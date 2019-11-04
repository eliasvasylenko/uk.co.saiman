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

import static java.util.Collections.emptyList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import uk.co.saiman.msapex.editor.Editor;
import uk.co.saiman.msapex.editor.EditorProvider;
import uk.co.saiman.msapex.editor.EditorService;

public class EditorAddon implements EditorService {
  private static final String EDITOR_STACK_ID = "uk.co.saiman.editor.partstack";

  private final Set<EditorProvider> editorProviders = new HashSet<>();

  @Inject
  private MApplication application;
  @Inject
  private EPartService partService;
  @Inject
  private EModelService modelService;

  @PostConstruct
  void initialize(IEclipseContext context) {
    context.set(EditorService.class, this);
  }

  @Override
  public void registerProvider(EditorProvider editorProvider) {
    editorProviders.add(editorProvider);
  }

  @Override
  public void unregisterProvider(EditorProvider editorProvider) {
    editorProviders.remove(editorProvider);
  }

  @Override
  public void open(Editor editor) {
    List<MPartStack> editorStacks = modelService
        .findElements(application, EDITOR_STACK_ID, MPartStack.class, emptyList());
    if (!editorStacks.isEmpty()) {
      editorStacks.get(0).getChildren().add(editor.getPart());
      partService.activate(editor.getPart());
    }
  }

  @Override
  public Stream<EditorProvider> getEditorProviders() {
    return editorProviders.stream().map(this::mock);
  }

  private EditorProvider mock(EditorProvider editorProvider) {
    return new EditorProvider() {
      @Override
      public boolean isApplicable(Object contextValue) {
        return editorProvider.isApplicable(contextValue);
      }

      @Override
      public Editor getEditorPart(Object contextValue) {
        return mock(editorProvider.getEditorPart(contextValue));
      }

      @Override
      public String getContextKey() {
        return editorProvider.getContextKey();
      }
    };
  }

  private Editor mock(Editor editor) {
    return new Editor() {
      @Override
      public MPart getPart() {
        /*
         * TODO adjust precedence
         */
        return editor.getPart();
      }

      @Override
      public String getLabel() {
        return editor.getLabel();
      }

      @Override
      public String getDescription() {
        return editor.getDescription();
      }

      @Override
      public String getIconURI() {
        return editor.getIconURI();
      }
    };
  }
}
