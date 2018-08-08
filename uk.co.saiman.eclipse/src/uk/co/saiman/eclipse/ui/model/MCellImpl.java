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
package uk.co.saiman.eclipse.ui.model;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.component.annotations.Deactivate;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.eclipse.ui.TransferFormat;

/**
 * A basic implementation of a cell specialization. Ultimately, according to the
 * intended documented plan for {@link MCell}, this will not be needed as the E4
 * model service will provide implementations.
 * 
 * @author Elias N Vasylenko
 */
public class MCellImpl implements MHandledCell {
  private final String id;
  private final Class<?> contributionClass;
  private final List<MediaType> mediaTypes;

  private boolean editable;
  private List<TransferFormat<?>> transferFormats;

  private MCellImpl specialized;
  private LinkedHashSet<MCellImpl> specializations;

  private MCellImpl parent;
  private List<MCellImpl> children;

  private final String popupMenuId;
  private MPopupMenu popupMenu;
  private final String commandId;
  private MCommand command;
  private ParameterizedCommand parameterizedCommand;

  private IEclipseContext context;
  private Object contributionObject;

  public MCellImpl(String id, Class<?> contributionClass, MediaType... mediaTypes) {
    this(id, contributionClass, null, null, mediaTypes);
  }

  public MCellImpl(
      String id,
      Class<?> contributionClass,
      String popupMenuId,
      String commandId,
      MediaType... mediaTypes) {
    this.id = requireNonNull(id);
    this.contributionClass = contributionClass;
    this.mediaTypes = unmodifiableList(asList(mediaTypes));
    this.transferFormats = new ArrayList<>();
    this.specializations = new LinkedHashSet<>();
    this.children = new ArrayList<>();
    this.popupMenuId = popupMenuId;
    this.commandId = commandId;
  }

  void initialize(IEclipseContext context) {
    if (this.context == null) {
      context = context.createChild(id);
      this.context = context;

      context.set(MCell.class, this);

      initializeMediaTypes();
      initializeCommand();
      initializePopupMenu();
      initializeContribution();
      initializeChildren();
    }
  }

  private void initializeMediaTypes() {
    for (MediaType mediaType : mediaTypes) {
      // TODO find a transferformat which fulfils this and add it.
    }
  }

  private void initializeCommand() {
    if (commandId != null) {
      EModelService modelService = context.get(EModelService.class);
      ECommandService commandService = context.get(ECommandService.class);
      MPart part = context.get(MPart.class);
      setCommand(modelService.findElements(part, commandId, MCommand.class, emptyList()).get(0));
      setWbCommand(commandService.createCommand(commandId, emptyMap()));
    }
  }

  private void initializePopupMenu() {
    if (popupMenuId != null) {
      EModelService modelService = context.get(EModelService.class);
      setPopupMenu(
          (MPopupMenu) modelService
              .cloneSnippet(context.get(MApplication.class), popupMenuId, null));
    }
  }

  private void initializeContribution() {
    if (contributionClass != null) {
      this.contributionObject = ContextInjectionFactory.make(contributionClass, context);
    }
  }

  private void initializeChildren() {
    for (MCellImpl specialization : specializations) {
      specialization.initialize(context);
    }
    for (MCellImpl child : children) {
      child.initialize(context);
    }
  }

  @Override
  public Class<?> getContributionClass() {
    return contributionClass;
  }

  @Override
  public IEclipseContext getContext() {
    return context;
  }

  @Override
  public Object getObject() {
    return contributionObject;
  }

  @Deactivate
  void destroy() {
    setParent(null);
  }

  @Override
  public String getElementId() {
    return id;
  }

  @Override
  public MCell getSpecialized() {
    return specialized;
  }

  @Override
  public void setSpecialized(MCell specialized) {
    MCellImpl newSpecialized = (MCellImpl) specialized;

    if (this.specialized != null) {
      this.specialized.removeSpecialization(this);
    }
    if (newSpecialized != null) {
      newSpecialized.addSpecialization(this);
    }

    this.specialized = newSpecialized;
  }

  private synchronized void addSpecialization(MCellImpl mCellImpl) {
    specializations.add(this);
  }

  private synchronized void removeSpecialization(MCellImpl mCellImpl) {
    specializations.remove(this);
  }

  @Override
  public synchronized List<MCell> getSpecializations() {
    return new ArrayList<>(specializations);
  }

  @Override
  public boolean isEditable() {
    return editable;
  }

  @Override
  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  @Override
  public List<MCell> getChildren() {
    return new ArrayList<>(children);
  }

  @Override
  public MCell getParent() {
    return parent;
  }

  @Override
  public void setParent(MCell parent) {
    MCellImpl newParent = (MCellImpl) parent;

    if (this.parent != null) {
      this.parent.removeChild(this);
    }
    if (newParent != null) {
      newParent.addChild(this);
    }

    this.parent = newParent;
  }

  private void addChild(MCell child) {
    children.add((MCellImpl) child);
  }

  private void removeChild(MCell child) {
    children.remove(child);
  }

  @Override
  public List<MediaType> getMediaTypes() {
    return mediaTypes;
  }

  @Override
  public List<TransferFormat<?>> getTransferFormats() {
    return transferFormats;
  }

  @Override
  public MPopupMenu getPopupMenu() {
    return popupMenu;
  }

  @Override
  public void setPopupMenu(MPopupMenu popupMenu) {
    this.popupMenu = popupMenu;
  }

  @Override
  public MCommand getCommand() {
    return command;
  }

  @Override
  public void setCommand(MCommand command) {
    this.command = command;
  }

  @Override
  public ParameterizedCommand getWbCommand() {
    return parameterizedCommand;
  }

  @Override
  public void setWbCommand(ParameterizedCommand parameterizedCommand) {
    this.parameterizedCommand = parameterizedCommand;
  }
}
