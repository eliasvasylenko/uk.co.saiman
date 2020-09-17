/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.ui.edit.
 *
 * uk.co.saiman.eclipse.ui.edit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui.edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.MPackage.eINSTANCE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import uk.co.saiman.eclipse.model.ui.provider.editor.ListComponentManager.Type;

public class TreeEditor extends AbstractEditor {
  private ListComponentManager childrenComponent;
  @SuppressWarnings("rawtypes")
  private final IListProperty CHILDREN = EMFProperties
      .list(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);

  @PostConstruct
  void init() {
    childrenComponent = new ListComponentManager(
        this,
        Messages,
        UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
        this::handleCreate,
        new Type(
            getString("_UI_Cell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/Cell.gif"),
            eINSTANCE.getCell()),
        new Type(
            getString("_UI_HandledCell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/HandledCell.gif"),
            eINSTANCE.getHandledCell()),
        new Type(
            getString("_UI_EditableCell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/EditableCell.gif"),
            eINSTANCE.getEditableCell()));
  }

  @Override
  protected void refreshEditor() {
    // listEditor.getViewer().setInput(o.getList());
  }

  private EObject handleCreate(Type type) {
    final EObject handler = EcoreUtil.create(type.eClass);
    setElementId(handler);
    return handler;
  }

  @Override
  public String getLabel(Object element) {
    return getString("_UI_Tree_type");
  }

  @Override
  public Image getImage(Object element) {
    return ImageDescriptor
        .createFromFile(VListEditor.class, "/icons/full/obj16/Tree.gif")
        .createImage();
  }

  @Override
  public String getDetailLabel(Object element) {
    final MContribution contrib = (MContribution) element;
    if (contrib.getContributionURI() != null && contrib.getContributionURI().trim().length() > 0) {
      return contrib
          .getContributionURI()
          .substring(contrib.getContributionURI().lastIndexOf('/') + 1);
    }
    return null;
  }

  @Override
  public String getDescription(Object element) {
    return getString("_UI_Tree_editor_description");
  }

  @Override
  protected void createDefaultControls(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {
    createElementIdControl(parent, context, master, textProp);
    createContributionControl(parent, context);
    createEditableControl(parent, context);
    createChildrenControl(parent);
    createPersistedStateControl(parent);
  }

  @SuppressWarnings("unchecked")
  private void createChildrenControl(Composite parent) {
    childrenComponent.createForm(parent);
    childrenComponent.getViewer().setInput(CHILDREN.observeDetail(getMaster()));
  }

  private void createEditableControl(Composite parent, EMFDataBindingContext context) {
    ControlFactory
        .createCheckBox(
            parent,
            getString("_UI_Tree_editable_feature"),
            getMaster(),
            context,
            WidgetProperties.selection(),
            EMFEditProperties.value(getEditingDomain(), eINSTANCE.getTree_Editable()));
  }

  @Override
  protected void createSupplementaryControls(Composite parent) {
    createContextVariablesControl(parent);
    createTagsControl(parent);
  }

  @Override
  public List<Action> getActions(Object element) {
    List<Action> actions = new ArrayList<>(super.getActions(element));
    actions.addAll(childrenComponent.getActions());
    return actions;
  }

  @SuppressWarnings("unchecked")
  @Override
  public IObservableList<?> getChildList(Object element) {
    return CHILDREN.observe(element);
  }
}
