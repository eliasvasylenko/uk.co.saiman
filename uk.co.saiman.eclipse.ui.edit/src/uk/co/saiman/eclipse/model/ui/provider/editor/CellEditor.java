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

import static org.eclipse.e4.tools.emf.ui.internal.common.ModelEditor.VIRTUAL_HANDLER;
import static uk.co.saiman.eclipse.model.ui.MPackage.eINSTANCE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.tools.emf.ui.common.Util;
import org.eclipse.e4.tools.emf.ui.internal.ResourceProvider;
import org.eclipse.e4.tools.emf.ui.internal.common.VirtualEntry;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.commands.impl.CommandsPackageImpl;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.MUiFactory;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.IEMFValueProperty;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MPackage;
import uk.co.saiman.eclipse.model.ui.provider.editor.ListComponentManager.Type;

public class CellEditor extends AbstractEditor {
  private final IEMFValueProperty UI_ELEMENT__VISIBLE_WHEN = EMFProperties
      .value(UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN);

  @SuppressWarnings("rawtypes")
  private final IListProperty CHILDREN = EMFProperties
      .list(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);
  private ListComponentManager childrenComponent;

  @SuppressWarnings("rawtypes")
  private final IListProperty HANDLER_CONTAINER__HANDLERS = EMFProperties
      .list(CommandsPackageImpl.Literals.HANDLER_CONTAINER__HANDLERS);

  @SuppressWarnings("unchecked")
  private static final IValueProperty<MCell, ?> POPUP_MENU = EMFProperties
      .value(MPackage.eINSTANCE.getCell_PopupMenu());
  private Button createRemovePopupMenu;

  private Action addExpression;

  @PostConstruct
  void init() {
    childrenComponent = new ListComponentManager(
        this,
        Messages,
        UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
        this::createChild,
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

    addExpression = new Action(
        Messages.MenuItemEditor_AddCoreExpression,
        createImageDescriptor(ResourceProvider.IMG_CoreExpression)) {
      @Override
      public void run() {
        final MUIElement e = (MUIElement) getMaster().getValue();
        final Command cmd = SetCommand
            .create(
                getEditingDomain(),
                e,
                UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN,
                MUiFactory.INSTANCE.createCoreExpression());
        if (cmd.canExecute()) {
          getEditingDomain().getCommandStack().execute(cmd);
        }
      }
    };
  }

  private EObject createChild(Type type) {
    final EObject handler = EcoreUtil.create(type.eClass);
    setElementId(handler);
    return handler;
  }

  @Override
  public String getLabel(Object element) {
    return getString("_UI_Cell_type");
  }

  @Override
  public Image getImage(Object element) {
    return ImageDescriptor
        .createFromFile(VListEditor.class, "/icons/full/obj16/Cell.gif")
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
    return getString("_UI_Cell_editor_description");
  }

  @Override
  protected void refreshEditor() {
    if (createRemovePopupMenu != null) {
      createRemovePopupMenu.setSelection(((MCell) getMaster().getValue()).getPopupMenu() != null);
    }
  }

  @Override
  protected void createDefaultControls(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {
    createElementIdControl(parent, context, master, textProp);
    createLabelControls(parent, context, master, textProp);
    createContributionControl(parent, context);
    createRenderingControls(parent, context);
    createPopupMenuControl(parent);
    createChildrenControl(parent);
    createPersistedStateControl(parent);
    createContextPropertiesControl(parent);
  }

  protected void createRenderingControls(Composite parent, EMFDataBindingContext context) {
    createVisibleWhenControl(parent, context);

    ControlFactory
        .createCheckBox(
            parent,
            Messages.ModelTooling_UIElement_Visible,
            getMaster(),
            context,
            WidgetProperties.selection(),
            EMFEditProperties
                .value(getEditingDomain(), UiPackageImpl.Literals.UI_ELEMENT__VISIBLE));

    ControlFactory
        .createCheckBox(
            parent,
            Messages.ModelTooling_UIElement_ToBeRendered,
            getMaster(),
            context,
            WidgetProperties.selection(),
            EMFEditProperties
                .value(getEditingDomain(), UiPackageImpl.Literals.UI_ELEMENT__TO_BE_RENDERED));

  }

  @SuppressWarnings("unchecked")
  protected void createVisibleWhenControl(Composite parent, EMFDataBindingContext context) {
    final Label l = new Label(parent, SWT.NONE);
    l.setText(Messages.ModelTooling_UIElement_VisibleWhen);
    l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    final ComboViewer combo = new ComboViewer(parent);
    combo
        .getControl()
        .setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
    combo.setContentProvider(new ArrayContentProvider());
    combo.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        if (element instanceof EClass) {
          final EClass eClass = (EClass) element;
          return eClass.getName();
        }

        return super.getText(element);
      }
    });
    final List<Object> list = new ArrayList<>();
    list.add(Messages.MenuItemEditor_NoExpression);
    list.add(UiPackageImpl.Literals.CORE_EXPRESSION);
    list.add(UiPackageImpl.Literals.IMPERATIVE_EXPRESSION);
    list
        .addAll(
            getEditor()
                .getFeatureClasses(
                    UiPackageImpl.Literals.EXPRESSION,
                    UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN));
    combo.setInput(list);
    context
        .bindValue(
            ViewerProperties.singleSelection().observe(combo),
            EMFEditProperties
                .value(getEditingDomain(), UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN)
                .observeDetail(getMaster()),
            new UpdateValueStrategy<>().setConverter(new EClass2EObject()),
            new UpdateValueStrategy<>().setConverter(new EObject2EClass()));
  }

  @SuppressWarnings("unchecked")
  protected void createChildrenControl(Composite parent) {
    childrenComponent.createForm(parent);
    childrenComponent.getViewer().setInput(CHILDREN.observeDetail(getMaster()));
  }

  protected void createPopupMenuControl(Composite parent) {
    final Label l = new Label(parent, SWT.NONE);
    l.setText(getString("_UI_Cell_popupMenu_feature"));
    l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    createRemovePopupMenu = new Button(parent, SWT.CHECK);
    createRemovePopupMenu.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        final MCell window = (MCell) getMaster().getValue();
        if (window.getPopupMenu() != null) {
          removePopupMenu();
        } else {
          addPopupMenu();
        }
      }
    });
    createRemovePopupMenu
        .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
  }

  private void addPopupMenu() {
    final MPopupMenu menu = MMenuFactory.INSTANCE.createPopupMenu();
    setElementId(menu);

    final Command cmd = SetCommand
        .create(getEditingDomain(), getMaster().getValue(), eINSTANCE.getCell_PopupMenu(), menu);
    if (cmd.canExecute()) {
      getEditingDomain().getCommandStack().execute(cmd);
    }
  }

  private void removePopupMenu() {
    final Command cmd = SetCommand
        .create(getEditingDomain(), getMaster().getValue(), eINSTANCE.getCell_PopupMenu(), null);
    if (cmd.canExecute()) {
      getEditingDomain().getCommandStack().execute(cmd);
    }
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
    if (((MCell) getMaster().getValue()).getVisibleWhen() == null) {
      actions.add(addExpression);
    }
    return actions;
  }

  @SuppressWarnings("unchecked")
  @Override
  public IObservableList<?> getChildList(final Object element) {
    final WritableList<Object> list = new WritableList<>();

    if (getEditor().isModelFragment() && Util.isImport((EObject) element)) {
      return list;
    }

    list
        .add(
            new VirtualEntry<Object,Object>(
                VIRTUAL_HANDLER,
                HANDLER_CONTAINER__HANDLERS,
                element,
                Messages.PartEditor_Handlers) {

              @Override
              protected boolean accepted(Object o) {
                return true;
              }

            });

    list.addAll(CHILDREN.getList(element));
    CHILDREN.observe(element).addChangeListener(event -> {
      if (!event.getObservable().isDisposed()) {
        while (list.size() > 1) {
          list.remove(1);
        }
        list.addAll(CHILDREN.getList(element));
      }
    });

    final MCell cell = (MCell) element;
    if (cell.getPopupMenu() != null) {
      list.add(0, cell.getPopupMenu());
    }
    POPUP_MENU.observe(cell).addValueChangeListener(event -> {
      if (event.diff.getOldValue() != null) {
        list.remove(event.diff.getOldValue());
        if (getMaster().getValue() == element && !createRemovePopupMenu.isDisposed()) {
          createRemovePopupMenu.setSelection(false);
        }
      }

      if (event.diff.getNewValue() != null) {
        list.add(0, event.diff.getNewValue());
        if (getMaster().getValue() == element && !createRemovePopupMenu.isDisposed()) {
          createRemovePopupMenu.setSelection(true);
        }
      }
    });

    if (((MCell) element).getVisibleWhen() != null) {
      list.add(0, ((MCell) element).getVisibleWhen());
    }
    UI_ELEMENT__VISIBLE_WHEN.observe(element).addValueChangeListener(event -> {
      if (event.diff.getOldValue() != null) {
        list.remove(event.diff.getOldValue());
      }

      if (event.diff.getNewValue() != null) {
        list.add(0, event.diff.getNewValue());
      }
    });

    return list;
  }

  class EObject2EClass extends Converter<Object, Object> {
    public EObject2EClass() {
      super(EObject.class, EClass.class);
    }

    @Override
    public Object convert(Object fromObject) {
      if (fromObject == null) {
        return Messages.MenuItemEditor_NoExpression;
      }
      return ((EObject) fromObject).eClass();
    }
  }

  class EClass2EObject extends Converter<Object, Object> {
    public EClass2EObject() {
      super(EClass.class, EObject.class);
    }

    @Override
    public Object convert(Object fromObject) {
      if (fromObject == null
          || fromObject.toString().equals(Messages.MenuItemEditor_NoExpression)) {
        return null;
      }
      return EcoreUtil.create((EClass) fromObject);
    }
  }
}
