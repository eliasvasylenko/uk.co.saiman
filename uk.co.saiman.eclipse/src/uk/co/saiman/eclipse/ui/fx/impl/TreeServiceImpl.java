package uk.co.saiman.eclipse.ui.fx.impl;

import static org.eclipse.e4.core.contexts.ContextInjectionFactory.make;
import static uk.co.saiman.fx.FxmlLoadBuilder.buildWith;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.OSGiBundle;
import org.eclipse.fx.core.di.LocalInstance;
import org.eclipse.fx.core.di.Service;
import org.osgi.framework.Bundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeView;
import uk.co.saiman.eclipse.ui.fx.TreeService;
import uk.co.saiman.eclipse.ui.model.MTree;
import uk.co.saiman.eclipse.ui.model.MTreeImpl;

@Creatable
public class TreeServiceImpl implements TreeService {
  @Inject
  @Service
  private List<MTree> trees;

  @Inject
  private IEclipseContext context;

  @Inject
  @OSGiBundle
  private Bundle bundle;

  @Override
  public TreeView<?> createTree(MTree treeModel, Object root) {
    MTreeImpl tree = (MTreeImpl) treeModel;
    FXMLLoader loader = make(LoaderInjector.class, tree.getContext(), context).loader;
    return buildWith(loader).controller(ModularTreeController.class).loadRoot();
  }

  @Override
  public MTreeImpl getTree(String treeModelId) {
    return trees
        .stream()
        .filter(t -> t.getElementId().equals(treeModelId))
        .findAny()
        .filter(MTreeImpl.class::isInstance)
        .map(MTreeImpl.class::cast)
        .map(tree -> {
          tree.initialize(context);
          return tree;
        })
        .orElse(null);
  }

  @Creatable
  static class LoaderInjector {
    @Inject
    @LocalInstance
    private FXMLLoader loader;
  }
}
