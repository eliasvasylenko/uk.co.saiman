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

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.log.Log.Level;

public class XmlExperiment extends XmlExperimentNode<ExperimentConfiguration, Void>
    implements Experiment {
  static final String EXPERIMENT_EXTENSION = ".exml";

  private static final String EXPERIMENT_ELEMENT = "experiment";

  protected XmlExperiment(XmlWorkspace workspace, String id) {
    super(workspace, workspace.getExperimentRootType(), id);
    workspace.addExperimentImpl(this);
    save();
  }

  protected XmlExperiment(XmlWorkspace workspace, Element root) throws XPathExpressionException {
    this(workspace, root, XPathFactory.newInstance().newXPath());
  }

  private XmlExperiment(XmlWorkspace workspace, Element root, XPath xPath)
      throws XPathExpressionException {
    super(workspace, workspace.getExperimentRootType(), root, xPath);
    workspace.addExperimentImpl(this);
  }

  private Path getPath() {
    return getWorkspace().getRootPath().resolve(getId() + EXPERIMENT_EXTENSION);
  }

  protected Path save() {
    Path location = getPath();

    try (OutputStream output = newOutputStream(location, CREATE, TRUNCATE_EXISTING, WRITE)) {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element element = document.createElement(EXPERIMENT_ELEMENT);
      saveNode(element);
      document.appendChild(element);

      Transformer tr = TransformerFactory.newInstance().newTransformer();
      tr.setOutputProperty(OutputKeys.INDENT, "yes");
      tr.setOutputProperty(OutputKeys.METHOD, "xml");
      tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      tr.transform(new DOMSource(document), new StreamResult(output));

      return location;
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          getText().exception().cannotPersistState(this),
          e);
      getWorkspace().getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }

  @Override
  public void remove() {
    assertAvailable();
    setDisposed();

    if (!getWorkspace().removeExperiment(getExperiment())) {
      ExperimentException e = new ExperimentException(
          getText().exception().experimentDoesNotExist(getId()));
      getWorkspace().getLog().log(Level.ERROR, e);
      throw e;
    }

    try {
      Files.delete(getPath());
    } catch (IOException e) {
      ExperimentException ee = new ExperimentException(
          getText().exception().cannotRemoveExperiment(this),
          e);
      getWorkspace().getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }

  protected static XmlExperiment load(XmlWorkspace workspace, String name) {
    return load(workspace, workspace.getRootPath().resolve(name + EXPERIMENT_EXTENSION));
  }

  static XmlExperiment load(XmlWorkspace workspace, Path path) {
    try (InputStream input = newInputStream(path, READ)) {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

      Element root = document.getDocumentElement();
      XmlExperiment experiment = new XmlExperiment(workspace, root);

      return experiment;
    } catch (Exception e) {
      ExperimentException ee = new ExperimentException(
          workspace.getText().exception().cannotLoadExperiment(path),
          e);
      workspace.getLog().log(Level.ERROR, ee);
      throw ee;
    }
  }
}
