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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.msapex.ResultEditorPart;

@Creatable
@Singleton
public class ResultEditorManager {
	@Inject
	public EPartService partService;

	private Map<ExperimentResult<?>, ResultEditorPart<?>> editorParts;

	public ResultEditorManager() {
		editorParts = new HashMap<>();
	}

	public synchronized <T> ResultEditorPart<T> openEditor(ExperimentResult<T> result) {
		@SuppressWarnings("unchecked")
		ResultEditorPart<T> editorPart = (ResultEditorPart<T>) editorParts
				.computeIfAbsent(result, r -> createEditor(result));
		partService.activate(editorPart.getPart());

		return editorPart;
	}

	@Inject
	@Optional
	private synchronized void subscribeTopicTodoUpdated(
			@UIEventTopic(UIEvents.UIElement.TOPIC_TOBERENDERED) Map<String, ?> data) {
		if (Boolean.FALSE.equals(data.get(UIEvents.EventTags.NEW_VALUE))) {
			Object element = data.get(UIEvents.EventTags.ELEMENT);

			if (element instanceof MPart) {
				Object controller = ((MPart) element).getObject();

				if (controller instanceof ResultEditorPart<?>) {
					removeEditor((ResultEditorPart<?>) controller);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> ResultEditorPart<T> createEditor(ExperimentResult<T> data) {
		MPart editorPart = partService.createPart(ResultEditorPartImpl.PART_ID);
		partService.showPart(editorPart, PartState.ACTIVATE);

		ResultEditorPart<T> controller = (ResultEditorPart<T>) editorPart.getObject();
		controller.setData(data);

		return controller;
	}

	private void removeEditor(ResultEditorPart<?> controller) {
		editorParts.remove(controller.getData());
	}
}
