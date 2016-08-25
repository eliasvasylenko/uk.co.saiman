/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.experiment.
 *
 * uk.co.saiman.msapex.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.experiment;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.strangeskies.eclipse.ObservableService;
import uk.co.strangeskies.fx.ModularTreeView;
import uk.co.strangeskies.fx.TreeItemImpl;
import uk.co.strangeskies.reflection.TypeToken;
import uk.co.strangeskies.reflection.TypedObject;

public class ExperimentTreeController {
	@FXML
	private ModularTreeView treeView;

	private ExperimentWorkspace workspace;

	@Inject
	IEclipseContext context;

	@Inject
	@ObservableService
	ObservableList<ExperimentTreeContributor> contributions;

	@FXML
	void initialize() {
		contributions.addListener((ListChangeListener<ExperimentTreeContributor>) change -> {
			while (change.next())
				if (change.wasAdded())
					change.getAddedSubList().forEach(this::contribute);
		});

		contributions.stream().forEach(this::contribute);
	}

	protected void contribute(ExperimentTreeContributor contributor) {
		treeView.addContribution(ContextInjectionFactory.make(contributor.getContribution(), context));
	}

	public void setWorkspace(ExperimentWorkspace workspace) {
		// prepare workspace
		this.workspace = workspace;

		treeView.setRootData(new TypeToken<ExperimentWorkspace>() {}.typedObject(workspace));
	}

	public void refresh() {
		((TreeItemImpl<?>) treeView.getRoot()).rebuildChildren();
	}

	public TreeItemImpl<?> getSelection() {
		return (TreeItemImpl<?>) treeView.getSelectionModel().getSelectedItem();
	}

	public TypedObject<?> getSelectionData() {
		return getSelection().getValue();
	}
}
