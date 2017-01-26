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

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.commands.MHandlerContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;

import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.msapex.ResultEditorPart;

@Creatable
@Singleton
public class ResultEditorManager {
	@Inject
	public EPartService partService;
	@Inject
	public IEventBroker eventBroker;

	private Map<MPart, ExperimentResult<?>> partResults;
	private Map<ExperimentResult<?>, ResultEditorPart<?>> editorParts;

	public ResultEditorManager() {
		partResults = new HashMap<>();
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
	private synchronized void initializeEditorContext(@UIEventTopic(UIEvents.Context.TOPIC_CONTEXT) Event event) {
		Object origin = event.getProperty(UIEvents.EventTags.ELEMENT);
		Object value = event.getProperty(UIEvents.EventTags.NEW_VALUE);

		if (origin instanceof MHandlerContainer && value instanceof IEclipseContext
				&& UIEvents.EventTypes.SET.equals(event.getProperty(UIEvents.EventTags.TYPE))) {
			IEclipseContext context = (IEclipseContext) value;
			MPart part = context.get(MPart.class);

			ExperimentResult<?> result = partResults.get(part);
			if (result != null) {
				context.set(ExperimentResult.class, result);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> ResultEditorPart<T> createEditor(ExperimentResult<T> data) {
		MPart editorPart = partService.createPart(ResultEditorPartImpl.PART_ID);
		partResults.put(editorPart, data);
		partService.showPart(editorPart, PartState.ACTIVATE);

		ResultEditorPart<T> controller = (ResultEditorPart<T>) editorPart.getObject();

		return controller;
	}

	protected void removeEditor(ResultEditorPart<?> controller) {
		editorParts.remove(controller.getData());
		partResults.remove(controller.getPart());
	}
}
