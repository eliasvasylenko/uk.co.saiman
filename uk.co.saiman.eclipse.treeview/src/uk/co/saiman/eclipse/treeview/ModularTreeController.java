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
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview;

import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.adapter.Adapter;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import uk.co.saiman.eclipse.service.ObservableService;
import uk.co.saiman.reflection.token.TypedReference;

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
public class ModularTreeController {
  private final StringProperty tableId = new SimpleStringProperty();

  @FXML
  private ModularTreeView modularTreeView;

  @Inject
  private IEclipseContext context;

  @Inject
  @ObservableService
  private ObservableList<TreeContribution> contributors;

  @Inject
  private ESelectionService selectionService;

  @Inject
  private Adapter adapter;

  /**
   * Instantiate a controller with the default id - the simple name of the class -
   * and no contribution filter.
   */
  public ModularTreeController() {
    tableId.set(getClass().getName());
  }

  /**
   * @param id
   *          the {@link #getId() ID} of the controller to create
   */
  public ModularTreeController(String id) {
    tableId.set(id);
  }

  @FXML
  void initialize() {
    contributors.addListener((ListChangeListener<TreeContribution>) change -> {
      while (change.next()) {
        if (change.wasAdded())
          change.getAddedSubList().forEach(this::prepareContribution);
      }
      refresh();
    });
    contributors.stream().forEach(this::prepareContribution);

    modularTreeView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          selectionService.setSelection(newValue);
        });

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

  protected void prepareContribution(TreeContribution contribution) {
    context.set(ModularTreeController.class, this);
    ContextInjectionFactory.inject(contribution, context);
  }

  /**
   * @return the currently selected tree item
   */
  public ModularTreeItem<?> getSelection() {
    return (ModularTreeItem<?>) modularTreeView.getSelectionModel().getSelectedItem();
  }

  /**
   * @return the currently selected tree item data
   */
  public TreeEntry<?> getSelectionData() {
    return getSelection().getValue();
  }

  /**
   * @param root
   *          the root object supplemented with its exact generic type
   */
  public void setRootData(TypedReference<?> root) {
    ModularTreeItem<?> rootItem = createRoot(root);
    rootItem.setExpanded(true);
    modularTreeView.setShowRoot(false);

    // add root
    modularTreeView.setRoot(rootItem);

    modularTreeView.refresh();
  }

  @SuppressWarnings("unchecked")
  /**
   * @param root
   *          the root object
   */
  public void setRootData(Object root) {
    setRootData(TypedReference.typedObject((Class<Object>) root.getClass(), root));
  }

  protected ModularTreeItem<?> createRoot(TypedReference<?> root) {
    return new ModularTreeItem<>(root, this);
  }

  <U> U adapt(TreeEntry<?> treeEntry, Class<U> adapterType) {
    return adapter.adapt(treeEntry.data(), adapterType);
  }

  IEclipseContext getContext() {
    return context;
  }

  protected ModularTreeItem<?> getRoot() {
    return (ModularTreeItem<?>) modularTreeView.getRoot();
  }

  public void refresh() {
    getRoot().getEntry().refresh(true);
  }

  public Stream<TreeContribution> getContributors() {
    return contributors.stream();
  }
}
