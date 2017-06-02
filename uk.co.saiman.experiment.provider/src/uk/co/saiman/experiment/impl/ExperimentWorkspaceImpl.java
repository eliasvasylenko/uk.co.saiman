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

import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static uk.co.strangeskies.collection.stream.StreamUtilities.upcastStream;
import static uk.co.strangeskies.text.properties.PropertyLoader.getDefaultProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentProperties;
import uk.co.saiman.experiment.ExperimentRoot;
import uk.co.saiman.experiment.ExperimentType;
import uk.co.saiman.experiment.ExperimentWorkspace;
import uk.co.saiman.experiment.PersistedState;
import uk.co.strangeskies.collection.stream.StreamUtilities;
import uk.co.strangeskies.log.Log;

/**
 * Reference implementation of {@link ExperimentWorkspace}.
 * 
 * @author Elias N Vasylenko
 */
public class ExperimentWorkspaceImpl implements ExperimentWorkspace {
	private static final String EXPERIMENT_EXTENSION = ".exml";

	private static final String EXPERIMENT_ELEMENT = "experiment";
	private static final String NODE_ELEMENT = "node";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String ID_ATTRIBUTE = "id";
	private static final String CONFIGURATION_ELEMENT = "configuration";

	private final ExperimentWorkspaceFactoryImpl factory;
	private final Path dataRoot;

	private final Set<ExperimentType<?>> experimentTypes = new HashSet<>();

	private final ExperimentRoot experimentRootType = new RootExperimentImpl(this);
	private final List<ExperimentImpl> experiments = new ArrayList<>();

	private final ExperimentProperties text;

	private final Lock processingLock = new ReentrantLock();

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param factory
	 *          the factory which produces the workspace
	 * @param workspaceRoot
	 *          the path of the workspace data
	 */
	public ExperimentWorkspaceImpl(ExperimentWorkspaceFactoryImpl factory, Path workspaceRoot) {
		this(factory, workspaceRoot, getDefaultProperties(ExperimentProperties.class));
	}

	/**
	 * Try to create a new experiment workspace over the given root path
	 * 
	 * @param factory
	 *          the factory which produces the workspace
	 * @param workspaceRoot
	 *          the path of the workspace data
	 * @param text
	 *          a localized text accessor implementation
	 */
	public ExperimentWorkspaceImpl(
			ExperimentWorkspaceFactoryImpl factory,
			Path workspaceRoot,
			ExperimentProperties text) {
		this.factory = factory;
		this.dataRoot = workspaceRoot;
		this.text = text;

		loadExperiments();
	}

	private void loadExperiments() {
		PathMatcher filter = dataRoot.getFileSystem().getPathMatcher("glob:*" + EXPERIMENT_EXTENSION);

		try (DirectoryStream<Path> stream = newDirectoryStream(
				dataRoot,
				file -> isRegularFile(file) && filter.matches(file))) {
			for (Path path : stream) {
				loadExperiment(path);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	Log getLog() {
		return factory.getLog();
	}

	ExperimentProperties getText() {
		return text;
	}

	@Override
	public Path getWorkspaceDataPath() {
		return dataRoot;
	}

	/*
	 * Root experiment types
	 */

	@Override
	public ExperimentRoot getExperimentRootType() {
		return experimentRootType;
	}

	@Override
	public Stream<Experiment> getExperiments() {
		return upcastStream(experiments.stream());
	}

	protected Stream<ExperimentImpl> getExperimentsImpl() {
		return experiments.stream();
	}

	@Override
	public Experiment addExperiment(String name) {
		return addExperiment(name, new PersistedStateImpl());
	}

	protected ExperimentImpl addExperiment(String name, PersistedStateImpl persistedState) {
		if (!ExperimentConfiguration.isNameValid(name)) {
			throw new ExperimentException(text.exception().invalidExperimentName(name));
		}

		ExperimentImpl experiment = new ExperimentImpl(experimentRootType, this, persistedState);
		experiments.add(experiment);
		experiment.getState().setName(name);
		experiment.saveExperiment();

		return experiment;
	}

	protected boolean removeExperiment(Experiment experiment) {
		return experiments.remove(experiment);
	}

	/*
	 * Child experiment types
	 */

	@Override
	public boolean registerExperimentType(ExperimentType<?> experimentType) {
		return experimentTypes.add(experimentType);
	}

	@Override
	public boolean unregisterExperimentType(ExperimentType<?> experimentType) {
		return experimentTypes.remove(experimentType);
	}

	@Override
	public Stream<ExperimentType<?>> getRegisteredExperimentTypes() {
		return Stream.concat(factory.getRegisteredExperimentTypes(), experimentTypes.stream());
	}

	protected void process(ExperimentNodeImpl<?, ?> node) {
		if (processingLock.tryLock()) {
			try {
				processImpl(node);
			} finally {
				processingLock.unlock();
			}
		} else {
			throw new ExperimentException(
					text.exception().cannotProcessExperimentConcurrently(node.getRoot()));
		}
	}

	private boolean processImpl(ExperimentNodeImpl<?, ?> node) {
		boolean success = StreamUtilities
				.reverse(node.getAncestorsImpl())
				.filter(ExperimentNodeImpl::execute)
				.count() > 0;

		if (success) {
			processChildren(node);
		}

		return success;
	}

	private void processChildren(ExperimentNodeImpl<?, ?> node) {
		node.getChildrenImpl().filter(ExperimentNodeImpl::execute).forEach(this::processChildren);
	}

	protected Path saveExperiment(ExperimentImpl experiment) {
		Path location = getWorkspaceDataPath().resolve(experiment.getID() + EXPERIMENT_EXTENSION);

		try (OutputStream output = newOutputStream(location, CREATE, TRUNCATE_EXISTING, WRITE)) {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element element = document.createElement(EXPERIMENT_ELEMENT);
			saveExperimentNode(document, element, experiment);
			document.appendChild(element);

			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			tr.transform(new DOMSource(document), new StreamResult(output));

			return location;
		} catch (Exception e) {
			throw new ExperimentException(text.exception().cannotPersistState(experiment), e);
		}
	}

	private void saveExperimentNode(Document document, Node parent, ExperimentNodeImpl<?, ?> node) {
		Element element = document.createElement(NODE_ELEMENT);

		element.setAttribute(TYPE_ATTRIBUTE, node.getType().getID());
		element.setAttribute(ID_ATTRIBUTE, node.getID());

		savePersistedState(element, node.persistedState());

		node.getChildrenImpl().forEach(child -> saveExperimentNode(document, element, child));

		parent.appendChild(element);
	}

	private void savePersistedState(Element parent, PersistedState persistedState) {
		Element configuration = parent.getOwnerDocument().createElement(CONFIGURATION_ELEMENT);
		parent.appendChild(configuration);
		persistedState.getStrings().forEach(key -> {
			configuration.setAttribute(key, persistedState.getString(key));
		});
	}

	protected ExperimentImpl loadExperiment(String name) {
		return loadExperiment(dataRoot.resolve(name + EXPERIMENT_EXTENSION));
	}

	private ExperimentImpl loadExperiment(Path path) {
		try (InputStream input = newInputStream(path, READ)) {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

			Element root = document.getDocumentElement();
			String id = root.getAttribute(ID_ATTRIBUTE);
			PersistedStateImpl persistedState = loadPersistedState(root);
			ExperimentImpl experiment = addExperiment(id, persistedState);

			return experiment;
		} catch (Exception e) {
			throw new ExperimentException(text.exception().cannotLoadExperiment(path), e);
		}
	}

	private PersistedStateImpl loadPersistedState(Element parent) {
		Element configuration = (Element) parent.getElementsByTagName(CONFIGURATION_ELEMENT).item(0);
		PersistedStateImpl persistedState = new PersistedStateImpl();
		NamedNodeMap attributes = configuration.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attribute = (Attr) attributes.item(i);
			persistedState.putString(attribute.getName(), attribute.getValue());
		}
		return persistedState;
	}
}
