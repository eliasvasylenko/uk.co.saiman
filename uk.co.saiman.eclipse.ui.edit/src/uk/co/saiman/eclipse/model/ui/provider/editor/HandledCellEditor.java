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

import javax.inject.Inject;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.tools.emf.ui.common.CommandToStringConverter;
import org.eclipse.e4.tools.emf.ui.common.IModelResource;
import org.eclipse.e4.tools.emf.ui.internal.ResourceProvider;
import org.eclipse.e4.tools.emf.ui.internal.common.ModelEditor;
import org.eclipse.e4.tools.emf.ui.internal.common.VirtualEntry;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory.TextPasteHandler;
import org.eclipse.e4.tools.emf.ui.internal.common.component.dialogs.HandledMenuItemCommandSelectionDialog;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditListProperty;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class HandledCellEditor extends CellEditor {
  private final IEMFEditListProperty HANDLED_ITEM__PARAMETERS = EMFEditProperties
      .list(getEditingDomain(), MenuPackageImpl.Literals.HANDLED_ITEM__PARAMETERS);

  @Inject
  private IModelResource resource;

  @Override
  public String getLabel(Object element) {
    return getString("_UI_HandledCell_type");
  }

  @Override
  public Image getImage(Object element) {
    return ImageDescriptor
        .createFromFile(VListEditor.class, "/icons/full/obj16/HandledCell.gif")
        .createImage();
  }

  @Override
  public String getDescription(Object element) {
    return getString("_UI_HandledCell_editor_description");
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
    createCommandControl(parent, context, master, textProp);
    createPopupMenuControl(parent);
    createChildrenControl(parent);
    createPersistedStateControl(parent);
    createContextPropertiesControl(parent);
  }

  @SuppressWarnings("unchecked")
  private void createCommandControl(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {
    Label l = new Label(parent, SWT.NONE);
    l.setText(Messages.HandledMenuItemEditor_Command);
    l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    Text t = new Text(parent, SWT.BORDER);
    TextPasteHandler.createFor(t);
    t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    t.setEditable(false);
    context
        .bindValue(
            textProp.observeDelayed(200, t),
            EMFEditProperties
                .value(getEditingDomain(), MenuPackageImpl.Literals.HANDLED_ITEM__COMMAND)
                .observeDetail(master),
            new UpdateValueStrategy(),
            new UpdateValueStrategy().setConverter(new CommandToStringConverter(Messages)));

    Button b = new Button(parent, SWT.PUSH | SWT.FLAT);
    b.setText(Messages.ModelTooling_Common_FindEllipsis);
    b.setImage(createImage(ResourceProvider.IMG_Obj16_zoom));
    b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
    b.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        final HandledMenuItemCommandSelectionDialog dialog = new HandledMenuItemCommandSelectionDialog(
            b.getShell(),
            (MHandledItem) getMaster().getValue(),
            resource,
            Messages);
        dialog.open();
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public IObservableList<?> getChildList(Object element) {
    final WritableList<Object> list = (WritableList<Object>) super.getChildList(element);

    list
        .add(
            new VirtualEntry<MParameter>(
                ModelEditor.VIRTUAL_PARAMETERS,
                HANDLED_ITEM__PARAMETERS,
                element,
                Messages.HandledMenuItemEditor_Parameters) {
              @Override
              protected boolean accepted(MParameter o) {
                return true;
              }
            });

    return list;
  }
}
