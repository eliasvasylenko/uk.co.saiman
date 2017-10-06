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
 * This file is part of uk.co.saiman.fx.
 *
 * uk.co.saiman.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.fx;

import static java.lang.Integer.compare;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import uk.co.saiman.reflection.token.TypedReference;

/**
 * An implementation of {@link TreeView} which allows for modular and extensible
 * specification of table structure.
 * 
 * @author Elias N Vasylenko
 */
public class ModularTreeView extends TreeView<TreeItemData<?>> {
  private final Map<TreeContribution<?>, Integer> contributionRankings;
  private List<TreeContribution<?>> orderedContributions;

  /**
   * Instantiate an empty tree view containing the
   * {@link DefaultTreeCellContribution default cell contribution} over a cell
   * factory which instantiates an empty {@link TreeCellImpl}.
   */
  public ModularTreeView() {
    FxmlLoadBuilder.build().object(this).load();
    setCellFactory(v -> new TreeCellImpl(this));

    contributionRankings = new HashMap<>();

    addContribution(new DefaultTreeCellContribution(), Integer.MIN_VALUE);

    setMinWidth(0);
    prefWidth(0);

    addEventHandler(KeyEvent.ANY, event -> {
      if (event.getCode() == KeyCode.CONTEXT_MENU) {
        event.consume();

        if (event.getEventType() == KeyEvent.KEY_RELEASED) {
          Node selectionBounds = this;

          Bounds sceneBounds = selectionBounds.localToScene(selectionBounds.getLayoutBounds());
          Bounds screenBounds = selectionBounds.localToScreen(selectionBounds.getLayoutBounds());

          PickResult pickResult = new PickResult(
              selectionBounds,
              sceneBounds.getMaxX(),
              sceneBounds.getMaxY());

          fireEvent(
              new ContextMenuEvent(
                  ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                  sceneBounds.getMaxX(),
                  sceneBounds.getMaxY(),
                  screenBounds.getMaxX(),
                  screenBounds.getMaxY(),
                  true,
                  pickResult));
        }
      }
    });
  }

  /**
   * @param root
   *          the root object supplemented with its exact generic type
   */
  public void setRootData(TypedReference<?> root) {
    TreeItemImpl<?> rootItem = createRoot(root);
    rootItem.setExpanded(true);
    setShowRoot(false);

    // add root
    setRoot(rootItem);

    refresh();
  }

  protected TreeItemImpl<?> createRoot(TypedReference<?> root) {
    return new TreeItemImpl<>(root, this);
  }

  @SuppressWarnings("unchecked")
  /**
   * @param root
   *          the root object
   */
  public void setRootData(Object root) {
    setRootData(TypedReference.typedObject((Class<Object>) root.getClass(), root));
  }

  protected final TreeItemImpl<?> getRootImpl() {
    return (TreeItemImpl<?>) getRoot();
  }

  /**
   * As per {@link #addContribution(TreeContribution, int)} with a ranking of 0.
   * 
   * @param contribution
   *          the contribution to add to the view
   * @return true if the contribution was successfully added, false otherwise
   */
  public boolean addContribution(TreeContribution<?> contribution) {
    return addContribution(contribution, 0);
  }

  /**
   * If the contribution is not present in the tree, add it at the given
   * ranking. If it is already present, modify the ranking to the given ranking.
   * If it is already present at the given ranking, do nothing.
   * 
   * @param contribution
   *          the contribution to add to the view
   * @param ranking
   *          the precedence ranking of the contribution
   * @return true if the contribution was successfully added or modified, false
   *         otherwise
   */
  public boolean addContribution(TreeContribution<?> contribution, int ranking) {
    boolean added = !((Integer) ranking).equals(contributionRankings.put(contribution, ranking));

    if (added) {
      refreshContributions();
    }

    return added;
  }

  /**
   * @param contribution
   *          the contribution to remove from the view
   * @return true if the contribution was successfully removed, false otherwise
   */
  public boolean removeContribution(TreeContribution<?> contribution) {
    boolean removed = contributionRankings.remove(contribution) != null;

    if (removed) {
      refreshContributions();
    }

    return removed;
  }

  private void refreshContributions() {
    orderedContributions = contributionRankings
        .entrySet()
        .stream()
        .sorted((a, b) -> compare(b.getValue(), a.getValue()))
        .map(e -> e.getKey())
        .collect(toList());
  }

  /**
   * @return all contributions added to the view in order of from most to least
   *         preferred
   */
  public Stream<TreeContribution<?>> getContributions() {
    return orderedContributions.stream();
  }

  @Override
  public void refresh() {
    getRootImpl().getData().refresh(true);
  }
}
