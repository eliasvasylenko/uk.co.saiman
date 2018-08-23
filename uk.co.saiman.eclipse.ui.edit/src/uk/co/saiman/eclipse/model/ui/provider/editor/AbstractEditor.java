package uk.co.saiman.eclipse.model.ui.provider.editor;

import javax.inject.Inject;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.emf.ui.common.Util;
import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory;
import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.co.saiman.eclipse.model.ui.Tree;

public abstract class AbstractEditor extends AbstractComponentEditor {
  private Composite composite;
  private EMFDataBindingContext context;

  @Inject
  @Optional
  private IProject project;

  @Inject
  IEclipseContext eclipseContext;

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
    return composite;
  }

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

    createContributedEditorTabs(folder, context, getMaster(), Tree.class);

    folder.setSelection(0);

    return folder;
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
