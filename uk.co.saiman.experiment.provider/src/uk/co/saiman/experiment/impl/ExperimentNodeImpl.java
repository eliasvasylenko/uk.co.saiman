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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static javax.xml.xpath.XPathConstants.NODESET;
import static uk.co.strangeskies.collection.stream.StreamUtilities.upcastStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentConfigurationContext;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentExecutionContext;
import uk.co.saiman.experiment.ExperimentLifecycleState;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentResult;
import uk.co.saiman.experiment.ExperimentResultManager;
import uk.co.saiman.experiment.ExperimentResultType;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.PersistedState;
import uk.co.strangeskies.log.Log.Level;
import uk.co.strangeskies.observable.ObservableProperty;
import uk.co.strangeskies.observable.ObservableValue;

/**
 * Reference implementation of {@link ExperimentNode}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          the type of the experiment type
 * @param <S>
 *          the type of the data describing the experiment configuration
 */
public class ExperimentNodeImpl<T extends ExperimentType<S>, S> implements ExperimentNode<T, S> {
	private static final String NODE_ELEMENT = "node";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String ID_ATTRIBUTE = "id";

	private final ExperimentWorkspaceImpl workspace;
	private final T type;
	private final ExperimentNodeImpl<?, ?> parent;

	private final List<ExperimentNodeImpl<?, ?>> children;

	private final ObservableProperty<ExperimentLifecycleState, ExperimentLifecycleState> lifecycleState;
	private final S state;

	private HashMap<ExperimentResultType<?>, ExperimentResultImpl<?>> results;

	private String id;
	private PersistedStateImpl persistedState;

	/**
	 * Try to create a new experiment node of the given type, and with the given
	 * parent.
	 * 
	 * @param type
	 *          the type of the experiment
	 * @param parent
	 *          the parent of the experiment
	 */
	protected ExperimentNodeImpl(
			T type,
			String id,
			ExperimentNodeImpl<?, ?> parent,
			PersistedStateImpl persistedState) {
		this(type, id, parent.workspace, parent, persistedState);

		if (!type.mayComeAfter(parent)) {
			throw new ExperimentException(getText().exception().typeMayNotSucceed(type, this));
		}
		parent.getAncestorsImpl().filter(a -> !a.type.mayComeBefore(parent, type)).forEach(a -> {
			throw new ExperimentException(getText().exception().typeMayNotSucceed(type, a));
		});

		parent.children.add(this);
	}

	protected ExperimentNodeImpl(
			T type,
			String id,
			ExperimentWorkspaceImpl workspace,
			PersistedStateImpl persistedState) {
		this(type, id, workspace, null, persistedState);
	}

	private ExperimentNodeImpl(
			T type,
			String id,
			ExperimentWorkspaceImpl workspace,
			ExperimentNodeImpl<?, ?> parent,
			PersistedStateImpl persistedState) {
		this.type = type;
		this.workspace = workspace;
		this.parent = parent;
		this.children = new ArrayList<>();
		setID(id);

		this.results = new HashMap<>();
		getType().getResultTypes().forEach(r -> results.put(r, new ExperimentResultImpl<>(this, r)));

		this.lifecycleState = ObservableProperty.over(ExperimentLifecycleState.PREPARATION);
		this.persistedState = persistedState;
		persistedState.addObserver(s -> getRootImpl().save());
		this.state = type.createState(createConfigurationContext());

		if (getID() == null) {
			throw new ExperimentException(getText().exception().invalidExperimentName(null));
		}
	}

	protected ExperimentProperties getText() {
		return workspace.getText();
	}

	@Override
	public String getID() {
		return id;
	}

	protected Stream<ExperimentNodeImpl<?, ?>> getAncestorsImpl() {
		return getAncestors().map(a -> (ExperimentNodeImpl<?, ?>) a);
	}

	@Override
	public S getState() {
		return state;
	}

	@Override
	public ExperimentWorkspace getExperimentWorkspace() {
		return workspace;
	}

	protected Path getResultDataPath() {
		return getParentDataPath().resolve(id);
	}

	protected PersistedStateImpl persistedState() {
		return persistedState;
	}

	private Path getParentDataPath() {
		return getParentImpl().map(p -> p.getResultDataPath()).orElse(workspace.getWorkspaceDataPath());
	}

	private Stream<? extends ExperimentNodeImpl<?, ?>> getSiblings() {
		return getParentImpl().map(p -> p.getChildrenImpl()).orElse(
				upcastStream(workspace.getExperimentsImpl()));
	}

	@Override
	public T getType() {
		return type;
	}

	protected Optional<ExperimentNodeImpl<?, ?>> getParentImpl() {
		return Optional.ofNullable(parent);
	}

	@Override
	public Optional<ExperimentNode<?, ?>> getParent() {
		return Optional.ofNullable(parent);
	}

	protected ExperimentImpl getRootImpl() {
		return (ExperimentImpl) ExperimentNode.super.getRoot();
	}

	@Override
	public void remove() {
		assertAvailable();

		if (parent != null) {
			if (!parent.children.remove(this)) {
				throw new ExperimentException(getText().exception().experimentDoesNotExist(this));
			}
		} else {
			if (!workspace.removeExperiment(getRoot())) {
				throw new ExperimentException(getText().exception().experimentDoesNotExist(this));
			}
		}

		getRootImpl().save();
		setDisposed();
	}

	private void setDisposed() {
		lifecycleState.set(ExperimentLifecycleState.DISPOSED);
		for (ExperimentNodeImpl<?, ?> child : children) {
			child.setDisposed();
		}
	}

	protected void assertAvailable() {
		if (lifecycleState.get() == ExperimentLifecycleState.DISPOSED) {
			throw new ExperimentException(getText().exception().experimentIsDisposed(this));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<ExperimentNode<?, ?>> getChildren() {
		return (Stream<ExperimentNode<?, ?>>) (Object) getChildrenImpl();
	}

	protected Stream<ExperimentNodeImpl<?, ?>> getChildrenImpl() {
		return children.stream();
	}

	@Override
	public Stream<ExperimentType<?>> getAvailableChildExperimentTypes() {
		return workspace.getRegisteredExperimentTypes().filter(
				type -> this.type.mayComeBefore(this, type) && type.mayComeAfter(this));
	}

	@Override
	public <U, E extends ExperimentType<U>> ExperimentNode<E, U> addChild(E childType) {
		ExperimentNode<E, U> child = loadChild(childType, null, new PersistedStateImpl());
		getRootImpl().save();
		return child;
	}

	protected <U, E extends ExperimentType<U>> ExperimentNodeImpl<E, U> loadChild(
			E childType,
			String id,
			PersistedStateImpl persistedState) {
		assertAvailable();
		return new ExperimentNodeImpl<>(childType, id, this, persistedState);
	}

	@Override
	public ObservableValue<ExperimentLifecycleState> lifecycleState() {
		return lifecycleState;
	}

	@Override
	public String toString() {
		return getID() + " : " + type.getName() + " [" + lifecycleState.get() + "]";
	}

	@Override
	public void process() {
		workspace.process(this);
	}

	protected boolean execute() {
		lifecycleState.set(ExperimentLifecycleState.PROCESSING);

		try {
			validate();

			Files.createDirectories(getResultDataPath());

			getType().execute(createExecutionContext());

			lifecycleState.set(ExperimentLifecycleState.COMPLETION);
			return true;
		} catch (Exception e) {
			workspace
					.getLog()
					.log(Level.ERROR, new ExperimentException(getText().failedExperimentExecution(this), e));
			lifecycleState.set(ExperimentLifecycleState.FAILURE);
			return false;
		}
	}

	private void validate() {
		if (id == null) {
			throw new ExperimentException(getText().invalidExperimentName(null));
		}
	}

	private ExperimentResultManager createResultManager() {
		return new ExperimentResultManager() {
			@Override
			public <U> ExperimentResult<U> get(ExperimentResultType<U> resultType) {
				return ExperimentNodeImpl.this.getResult(resultType);
			}

			@Override
			public <U> ExperimentResult<U> set(ExperimentResultType<U> resultType, U resultData) {
				return ExperimentNodeImpl.this.setResult(resultType, resultData);
			}

			@Override
			public Path dataPath() {
				return ExperimentNodeImpl.this.getResultDataPath();
			}
		};
	}

	private ExperimentExecutionContext<S> createExecutionContext() {
		return new ExperimentExecutionContext<S>() {
			@Override
			public ExperimentResultManager results() {
				return createResultManager();
			}

			@Override
			public ExperimentNodeImpl<?, S> node() {
				return ExperimentNodeImpl.this;
			}
		};
	}

	private ExperimentConfigurationContext<S> createConfigurationContext() {
		return new ExperimentConfigurationContext<S>() {
			@Override
			public ExperimentNodeImpl<?, S> node() {
				return ExperimentNodeImpl.this;
			}

			@Override
			public ExperimentResultManager results() {
				return createResultManager();
			}

			@Override
			public PersistedState persistedState() {
				return ExperimentNodeImpl.this.persistedState();
			}

			@Override
			public void setID(String id) {
				ExperimentNodeImpl.this.setID(id);
			}
		};
	}

	protected void setID(String id) {
		if (Objects.equals(id, this.id)) {
			return;

		} else if (!ExperimentConfiguration.isNameValid(id)) {
			throw new ExperimentException(getText().invalidExperimentName(id));

		} else if (getSiblings().anyMatch(s -> id.equals(s.getID()))) {
			throw new ExperimentException(getText().duplicateExperimentName(id));

		} else {
			Path newLocation = getParentDataPath().resolve(id);

			if (this.id != null) {
				if (Files.exists(newLocation)) {
					throw new ExperimentException(getText().dataAlreadyExists(newLocation));
				}

				Path oldLocation = getParentDataPath().resolve(this.id);
				try {
					Files.move(oldLocation, newLocation);
				} catch (IOException e) {
					throw new ExperimentException(getText().cannotMove(oldLocation, newLocation));
				}
			} else {
				try {
					Files.createDirectories(newLocation);
				} catch (IOException e) {
					throw new ExperimentException(getText().cannotCreate(newLocation), e);
				}
			}

			this.id = id;
		}
	}

	@Override
	public Stream<ExperimentResult<?>> getResults() {
		return results.values().stream().map(t -> (ExperimentResult<?>) t);
	}

	@Override
	public void clearResults() {
		results.values().forEach(r -> r.setData(null));
	}

	protected <U> ExperimentResultImpl<U> setResult(
			ExperimentResultType<U> resultType,
			U resultData) {
		ExperimentResultImpl<U> result = getResult(resultType);
		result.setData(resultData);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> ExperimentResultImpl<U> getResult(ExperimentResultType<U> resultType) {
		return (ExperimentResultImpl<U>) results.get(resultType);
	}

	@Override
	public ExperimentNode<T, S> copy() {
		return this;
	}

	protected void saveNode(Element element) {
		element.setAttribute(TYPE_ATTRIBUTE, getType().getID());
		element.setAttribute(ID_ATTRIBUTE, getID());

		persistedState().save(element);

		getChildrenImpl().forEach(
				child -> child.saveNode(
						(Element) element.appendChild(element.getOwnerDocument().createElement(NODE_ELEMENT))));
	}

	protected void loadChildNodes(Element parentElement, XPath xPath)
			throws XPathExpressionException {
		NodeList nodes = (NodeList) xPath.evaluate(NODE_ELEMENT, parentElement, NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			loadChildNode((Element) nodes.item(i), xPath);
		}
	}

	private void loadChildNode(Element element, XPath xPath) throws XPathExpressionException {
		String experimentID = element.getAttribute(ID_ATTRIBUTE);
		String experimentTypeID = element.getAttribute(TYPE_ATTRIBUTE);

		ExperimentType<?> experimentType = getAvailableChildExperimentTypes()
				.filter(e -> e.getID().equals(experimentTypeID))
				.findAny()
				.orElseGet(() -> new MissingExperimentTypeImpl(getText(), experimentTypeID));

		ExperimentNodeImpl<?, ?> node = loadChild(
				experimentType,
				experimentID,
				PersistedStateImpl.load(element, xPath));

		node.loadChildNodes(element, xPath);
	}
}
