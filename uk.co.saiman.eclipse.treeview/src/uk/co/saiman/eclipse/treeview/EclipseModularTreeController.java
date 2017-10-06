/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.
 *
 * uk.co.saiman.eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.osgi.framework.Constants;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import uk.co.saiman.eclipse.ObservableService;
import uk.co.saiman.fx.ModularTreeView;
import uk.co.saiman.fx.TreeContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeItemImpl;

/**
 * A controller over a {@link ModularTreeView modular tree view} for use within
 * an Eclipse RCP environment.
 * <p>
 * This class allows {@link TreeContribution tree contributions} to be
 * contributed via {@link TreeContribution contributors} so that the
 * contributions are instantiated according to an Eclipse injection context.
 * 
 * @author Elias N Vasylenko
 */
public class EclipseModularTreeController {
  private final StringProperty tableId = new SimpleStringProperty();

  @FXML
  private EclipseModularTreeView eclipseModularTree;

  @Inject
  private IEclipseContext context;

  /*
   * As we are injecting into the contributions from the eclipse context of the
   * tree we may only accept prototype scope services.
   */
  @Inject
  @ObservableService(target = "(" + Constants.SERVICE_SCOPE + "=" + Constants.SCOPE_PROTOTYPE + ")")
  private ObservableList<TreeContribution<?>> contributions;

  @Inject
  @ObservableService
  private ObservableList<EclipseTreeContribution> contributions2;

  @Inject
  private ESelectionService selectionService;

  @Inject
  private Adapter adapter;

  /**
   * Instantiate a controller with the default id - the simple name of the class
   * - and no contribution filter.
   */
  public EclipseModularTreeController() {
    tableId.set(getClass().getName());
  }

  /**
   * @param id
   *          the {@link #getId() ID} of the controller to create
   */
  public EclipseModularTreeController(String id) {
    tableId.set(id);
  }

  @FXML
  void initialize() {
    contributions.addListener((ListChangeListener<TreeContribution<?>>) change -> {
      while (change.next())
        if (change.wasAdded())
          change.getAddedSubList().forEach(this::prepareContribution);
      updateContributions();
    });
    contributions.stream().forEach(this::prepareContribution);
    updateContributions();

    eclipseModularTree.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          selectionService.setSelection(newValue);
        });

    eclipseModularTree.setAdapter(adapter);
  }

  @PreDestroy
  void destroy() {}

  /**
   * @return The ID property of the controller. This is used to allow
   *         {@link TreeContribution contributions} to filter which controllers
   *         they wish to contribute to.
   */
  public StringProperty getTableIdProperty() {
    return tableId;
  }

  /**
   * @return the current ID of the controller
   */
  public String getId() {
    return tableId.get();
  }

  /**
   * @param id
   *          the new ID for the controller
   */
  public void setId(String id) {
    tableId.set(id);
  }

  protected void prepareContribution(TreeContribution<?> contribution) {
    context.set(EclipseModularTreeController.class, this);
    ContextInjectionFactory.inject(contribution, context);
  }

  protected void updateContributions() {
    int ranking = 0;
    for (TreeContribution<?> contribution : contributions)
      eclipseModularTree.addContribution(contribution, ranking++);
  }

  /**
   * @return the modular tree view instance
   */
  public ModularTreeView getTreeView() {
    return eclipseModularTree;
  }

  /**
   * @return the currently selected tree item
   */
  public TreeItemImpl<?> getSelection() {
    return (TreeItemImpl<?>) eclipseModularTree.getSelectionModel().getSelectedItem();
  }

  /**
   * @return the currently selected tree item data
   */
  public TreeItemData<?> getSelectionData() {
    return getSelection().getValue();
  }
}
