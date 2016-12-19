/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.msapex.
 *
 * uk.co.saiman.experiment.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.msapex.impl;

import static uk.co.strangeskies.fx.FxmlLoadBuilder.buildWith;
import static uk.co.strangeskies.reflection.ConstraintFormula.Kind.LOOSE_COMPATIBILILTY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.fx.core.di.LocalInstance;
import org.osgi.framework.Constants;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.msapex.ResultEditorContribution;
import uk.co.saiman.experiment.msapex.ResultEditorPart;
import uk.co.strangeskies.eclipse.ObservableService;

public class ResultEditorPartImpl<T> implements ResultEditorPart<T> {
	@Inject
	IEclipseContext context;
	@Inject
	MPart part;
	@Inject
	MDirtyable dirty;
	@Inject
	ECommandService commandService;
	@Inject
	EHandlerService handlerService;

	@Inject
	@ObservableService(target = "(" + Constants.SERVICE_SCOPE + "=" + Constants.SCOPE_PROTOTYPE + ")")
	ObservableList<ResultEditorContribution<?>> editorContributions;

	@FXML
	private TabPane tabPane;

	private ExperimentResult<T> data;
	private final Map<ResultEditorContribution<? super T>, Tab> contributions;

	public ResultEditorPartImpl() {
		contributions = new LinkedHashMap<>();
	}

	@PostConstruct
	public void initialize(BorderPane container, @LocalInstance FXMLLoader loader) {
		container.setCenter(buildWith(loader).controller(ResultEditorPart.class, this).loadRoot());
	}

	@SuppressWarnings("unchecked")
	protected void contribute(ResultEditorContribution<?> contribution) {
		if (contribution
				.getResultType()
				.satisfiesConstraintFrom(LOOSE_COMPATIBILILTY, data.getResultType().getDataType())) {
			addContribution((ResultEditorContribution<? super T>) contribution);
		}
	}

	@Override
	public boolean addContribution(ResultEditorContribution<? super T> contribution) {
		boolean added = !contributions.containsKey(contribution);

		if (added) {
			context.set(ExperimentResult.class, data);
			ContextInjectionFactory.inject(contribution, context);

			Tab tab = new Tab(contribution.getName(), contribution.getContent());
			tab.setClosable(false);
			contributions.put(contribution, tab);
			tabPane.getTabs().add(tab);
		}

		return added;
	}

	@Override
	public boolean removeContribution(ResultEditorContribution<? super T> contribution) {
		Tab removed = contributions.remove(contribution);

		if (removed != null) {
			tabPane.getTabs().remove(removed);
		}

		return removed != null;
	}

	@Override
	public List<ResultEditorContribution<? super T>> getContributions() {
		return Collections.unmodifiableList(new ArrayList<>(contributions.keySet()));
	}

	@Override
	@Persist
	public void save() {
		// model.saveTodo(todo);
		dirty.setDirty(false);
	}

	@Focus
	public void onFocus() {
		// the following assumes that you have a Text field
		// called summary

		// txtSummary.setFocus();
	}

	@Override
	public ExperimentResult<T> getData() {
		return data;
	}

	@Override
	public void setData(ExperimentResult<T> data) {
		this.data = data;
		data.getExperimentNode().lifecycleState().addObserver(s -> System.out.println("? ok >"));
		data.getExperimentNode().lifecycleState().changes().addWeakObserver(
				this,
				p -> c -> p.updateExperimentState(c.previousValue(), c.newValue()));
		for (ResultEditorContribution<?> contribution : editorContributions) {
			contribute(contribution);
		}
	}

	protected void updateExperimentState(ExperimentLifecycleState previousState, ExperimentLifecycleState newState) {
		System.out.println(previousState + "    ->    " + newState);
	}

	@Override
	public MPart getPart() {
		return part;
	}
}
