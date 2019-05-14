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
package uk.co.saiman.eclipse.model;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.ui.model.application.MContribution;

import uk.co.saiman.log.Log;
import uk.co.saiman.osgi.ServiceIndex;

/**
 * Register a multiple-selection tracker for a given service index against an
 * application context contribution, such that the selection is persisted
 * between runs and is resilient against unavailability.
 *
 * @author Elias N Vasylenko
 */
public abstract class IndexedSelectionService<T> {
  private final String selectionKey;
  private final MContribution contribution;

  private final IEclipseContext context;
  private final Log log;

  private final ServiceIndex<?, String, T> index;
  private final Set<String> selectedIndices;

  protected IndexedSelectionService(
      String selectionKey,
      MContribution contribution,
      IEclipseContext context,
      Log log,
      ServiceIndex<?, String, T> index) {
    this.selectionKey = selectionKey;
    this.contribution = contribution;

    this.context = context;
    this.log = log;
    this.index = index;
    this.selectedIndices = new HashSet<>();
  }

  public static <T, U> void startIndexedSelectionService(
      String selectionKey,
      MContribution contribution,
      IEclipseContext context,
      Log log,
      ServiceIndex<?, String, T> index,
      BiConsumer<? super IEclipseContext, ? super Collection<? extends T>> selectionToContextType,
      Function<? super IEclipseContext, ? extends Collection<? extends T>> selectionFromContextType) {
    var service = new IndexedSelectionService<>(selectionKey, contribution, context, log, index) {
      @Override
      protected void selectionToContextType(
          IEclipseContext context,
          Collection<? extends T> selection) {
        selectionToContextType.accept(context, selection);
      }

      @Override
      protected Collection<? extends T> selectionFromContextType(IEclipseContext context) {
        return selectionFromContextType.apply(context);
      }
    };

    service.start();
  }

  protected void start() {
    java.util.Optional
        .ofNullable(contribution.getPersistedState().get(selectionKey))
        .stream()
        .flatMap(s -> Stream.of(s.split(",")))
        .map(String::strip)
        .filter(not(String::isBlank))
        .forEach(item -> selectedIndices.add(item));

    index.events().observe(i -> updateSelection());
    updateSelection();

    context.runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        /*
         * Here we look through all currently available items. If they are selected, we
         * add them to the default selection. If they are not selected, we remove them
         * from the default.
         */

        synchronized (IndexedSelectionService.this) {
          var selection = getSelection(context);

          index.records().forEach(record -> record.id().ifPresent(id -> {
            if (selection.contains(record.serviceObject())) {
              selectedIndices.add(id);
            } else {
              selectedIndices.remove(id);
            }
          }));

          contribution
              .getPersistedState()
              .put(selectionKey, selectedIndices.stream().collect(joining(",")));

          return true;
        }
      }
    });
  }

  protected abstract void selectionToContextType(
      IEclipseContext context,
      Collection<? extends T> selection);

  protected abstract Collection<? extends T> selectionFromContextType(IEclipseContext context);

  private synchronized void updateSelection() {
    /*
     * Here we look through all the items in the indexed selection, if they are
     * available we add them to the current selection, if they are unavailable we
     * remove them.
     */

    var selection = getSelection(context);

    boolean modified = false;

    for (String selectedIndex : selectedIndices) {
      var selectedRecord = index.get(selectedIndex).orElse(null);

      if (selectedRecord != null) {
        var selectedObject = selectedRecord.serviceObject();
        selectedIndices.add(selectedIndex);
        modified = selection.add(selectedObject) || modified;

      } else {
        var selectedObject = selectedIndices.remove(selectedIndex);
        modified = selection.remove(selectedObject) || modified;
      }
    }

    if (modified) {
      selectionToContextType(context, selection);
      context.processWaiting();
    }
  }

  private Set<T> getSelection(IEclipseContext context) {
    var selection = selectionFromContextType(context);
    return selection == null ? new HashSet<>() : new HashSet<>(selection);
  }
}
