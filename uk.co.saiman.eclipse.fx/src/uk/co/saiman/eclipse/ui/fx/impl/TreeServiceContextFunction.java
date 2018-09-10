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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;

import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TreeView;
import uk.co.saiman.eclipse.model.ui.Tree;
import uk.co.saiman.eclipse.ui.fx.TreeService;
import uk.co.saiman.eclipse.ui.fx.widget.WTree;

@Component(
    property = IContextFunction.SERVICE_CONTEXT_KEY
        + "="
        + TreeServiceContextFunction.TREE_SERVICE_KEY)
public class TreeServiceContextFunction implements IContextFunction {
  static final String TREE_SERVICE_KEY = "uk.co.saiman.eclipse.ui.fx.TreeService";

  public static class TreeServiceImpl implements TreeService {
    @Inject
    private IEclipseContext context;

    @Inject
    private MApplication application;

    @Inject
    private MWindow window;

    @Inject
    private EModelService modelService;

    @Inject
    @OSGiBundle
    private Bundle bundle;

    @Override
    public Control createTree(Tree treeModel, Parent owner) {
      Tree tree = (Tree) treeModel;

      modelService.hostElement(tree, window, owner, context);

      TreeView<?> treeView = (TreeView<?>) ((WTree<?>) tree.getWidget()).getWidget();
      return treeView;
    }

    @Override
    public Control createTree(String treeModelId, Parent owner) {
      return createTree(getTree(treeModelId), owner);
    }

    @Override
    public Tree getTree(String treeModelId) {
      return (Tree) modelService.cloneSnippet(application, treeModelId, window);
    }
  }

  @Override
  public TreeService compute(IEclipseContext context, String contextKey) {
    if (!TREE_SERVICE_KEY.equals(contextKey)) {
      return null;
    }

    return ContextInjectionFactory.make(TreeServiceImpl.class, context);
  }
}
