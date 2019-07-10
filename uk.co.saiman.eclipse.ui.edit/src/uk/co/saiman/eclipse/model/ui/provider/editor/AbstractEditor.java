/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static uk.co.saiman.eclipse.model.ui.provider.UISaimanEditPlugin.INSTANCE;

import javax.inject.Inject;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.emf.ui.common.ImageTooltip;
import org.eclipse.e4.tools.emf.ui.common.Util;
import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.tools.emf.ui.internal.ResourceProvider;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory.TextPasteHandler;
import org.eclipse.e4.tools.emf.ui.internal.common.component.dialogs.PartIconDialogEditor;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.co.saiman.eclipse.model.ui.MTree;

public abstract class AbstractEditor extends AbstractComponentEditor {
  private Composite composite;
  private EMFDataBindingContext context;

  @Inject
  @Optional
  private IProject project;

  @Inject
  private IEclipseContext eclipseContext;

  private StackLayout stackLayout;

  @Inject
  public AbstractEditor() {
    super();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Composite doGetEditor(Composite parent, Object object) {
    if (composite == null) {
      context = new EMFDataBindingContext();
      if (getEditor().isModelFragment()) {
        composite = new Composite(parent, SWT.NONE);
        stackLayout = new StackLayout();
        composite.setLayout(stackLayout);
        createForm(composite, context, getMaster(), false);
        createForm(composite, context, getMaster(), true);
      } else {
        composite = createForm(parent, context, getMaster(), false);
      }
    }

    if (getEditor().isModelFragment()) {
      Control topControl;
      if (Util.isImport((EObject) object)) {
        topControl = composite.getChildren()[1];
      } else {
        topControl = composite.getChildren()[0];
      }

      if (stackLayout.topControl != topControl) {
        stackLayout.topControl = topControl;
        composite.layout(true, true);
      }
    }

    getMaster().setValue(object);

    refreshEditor();

    return composite;
  }

  protected void refreshEditor() {}

  protected Composite createForm(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      boolean isImport) {
    final CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);

    CTabItem item = new CTabItem(folder, SWT.NONE);
    item.setText(Messages.ModelTooling_Common_TabDefault);

    parent = createScrollableContainer(folder);
    item.setControl(parent.getParent());

    final IWidgetValueProperty textProp = WidgetProperties.text(SWT.Modify);

    if (getEditor().isShowXMIId() || getEditor().isLiveModel()) {
      ControlFactory.createXMIId(parent, this);
    }

    if (getEditor().isModelFragment() && isImport) {
      ControlFactory.createFindImport(parent, Messages, this, context);
      folder.setSelection(0);
      return folder;
    }

    createDefaultControls(parent, context, master, textProp);

    item = new CTabItem(folder, SWT.NONE);
    item.setText(Messages.ModelTooling_Common_TabSupplementary);

    parent = createScrollableContainer(folder);
    item.setControl(parent.getParent());

    createSupplementaryControls(parent);

    createContributedEditorTabs(folder, context, getMaster(), MTree.class);

    folder.setSelection(0);

    return folder;
  }

  protected void createElementIdControl(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {
    ControlFactory
        .createTextField(
            parent,
            Messages.ModelTooling_Common_Id,
            master,
            context,
            textProp,
            EMFEditProperties
                .value(
                    getEditingDomain(),
                    ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID));
  }

  @SuppressWarnings("unchecked")
  protected void createLabelControls(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {
    ControlFactory
        .createTextField(
            parent,
            Messages.PartEditor_LabelLabel,
            master,
            context,
            textProp,
            EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_LABEL__LABEL));
    ControlFactory
        .createTextField(
            parent,
            Messages.ModelTooling_UIElement_AccessibilityPhrase,
            master,
            context,
            textProp,
            EMFEditProperties
                .value(
                    getEditingDomain(),
                    UiPackageImpl.Literals.UI_ELEMENT__ACCESSIBILITY_PHRASE));
    ControlFactory
        .createTextField(
            parent,
            Messages.PartEditor_Tooltip,
            master,
            context,
            textProp,
            EMFEditProperties.value(getEditingDomain(), UiPackageImpl.Literals.UI_LABEL__TOOLTIP));

    // ------------------------------------------------------------
    {
      final Label l = new Label(parent, SWT.NONE);
      l.setText(Messages.PartEditor_IconURI);
      l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
      l.setToolTipText(Messages.PartEditor_IconURI_Tooltip);

      final Text t = new Text(parent, SWT.BORDER);
      TextPasteHandler.createFor(t);
      t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      context
          .bindValue(
              textProp.observeDelayed(200, t),
              EMFEditProperties
                  .value(getEditingDomain(), UiPackageImpl.Literals.UI_LABEL__ICON_URI)
                  .observeDetail(master));

      new ImageTooltip(t, Messages, this);

      final Button b = new Button(parent, SWT.PUSH | SWT.FLAT);
      b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
      b.setImage(createImage(ResourceProvider.IMG_Obj16_zoom));
      b.setText(getString("_UI_FindEllipsis"));
      b.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          final PartIconDialogEditor dialog = new PartIconDialogEditor(
              b.getShell(),
              eclipseContext,
              project,
              getEditingDomain(),
              (MPart) getMaster().getValue(),
              Messages);
          dialog.open();
        }
      });
    }
  }

  protected void createContributionControl(Composite parent, EMFDataBindingContext context) {
    ControlFactory
        .createClassURIField(
            parent,
            Messages,
            this,
            Messages.AddonsEditor_ClassURI,
            ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI,
            getEditor().getContributionCreator(ApplicationPackageImpl.Literals.ADDON),
            getProject(),
            context,
            eclipseContext);
  }

  protected void createPersistedStateControl(Composite parent) {
    ControlFactory
        .createMapProperties(
            parent,
            Messages,
            this,
            Messages.ModelTooling_Contribution_PersistedState,
            ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__PERSISTED_STATE,
            VERTICAL_LIST_WIDGET_INDENT);
  }

  protected void createContextPropertiesControl(Composite parent) {
    ControlFactory
        .createMapProperties(
            parent,
            Messages,
            this,
            Messages.ModelTooling_Context_Properties,
            UiPackageImpl.Literals.CONTEXT__PROPERTIES,
            VERTICAL_LIST_WIDGET_INDENT);
  }

  protected void createContextVariablesControl(Composite parent) {
    ControlFactory
        .createStringListWidget(
            parent,
            Messages,
            this,
            Messages.ModelTooling_Context_Variables,
            Messages.ModelTooling_Context_Variables_Tooltip,
            UiPackageImpl.Literals.CONTEXT__VARIABLES,
            VERTICAL_LIST_WIDGET_INDENT);
  }

  protected void createTagsControl(Composite parent) {
    ControlFactory
        .createStringListWidget(
            parent,
            Messages,
            this,
            Messages.CategoryEditor_Tags,
            ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__TAGS,
            VERTICAL_LIST_WIDGET_INDENT);
  }

  protected String getString(String key) {
    return INSTANCE.getPluginResourceLocator().getString(key);
  }

  public IProject getProject() {
    return project;
  }

  protected abstract void createDefaultControls(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp);

  protected abstract void createSupplementaryControls(Composite parent);

  @Override
  public IObservableList<?> getChildList(Object element) {
    return null;
  }

  @Override
  public FeaturePath[] getLabelProperties() {
    return new FeaturePath[] {
        FeaturePath.fromList(ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI) };
  }
}
