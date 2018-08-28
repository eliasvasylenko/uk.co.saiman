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
/**
 */
package uk.co.saiman.eclipse.model.ui.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.e4.ui.model.application.MApplicationFactory;

import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;

import org.eclipse.e4.ui.model.application.ui.MUiFactory;

import org.eclipse.e4.ui.model.application.ui.advanced.MAdvancedFactory;

import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;

import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;

import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Factory;

/**
 * This is the item provider adapter for a {@link uk.co.saiman.eclipse.model.ui.Cell} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class CellItemProvider 
  extends ItemProviderAdapter
  implements
    IEditingDomainItemProvider,
    IStructuredItemContentProvider,
    ITreeItemContentProvider,
    IItemLabelProvider,
    IItemPropertySource {
  /**
   * This constructs an instance from a factory and a notifier.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CellItemProvider(AdapterFactory adapterFactory) {
    super(adapterFactory);
  }

  /**
   * This returns the property descriptors for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
    if (itemPropertyDescriptors == null) {
      super.getPropertyDescriptors(object);

      addLabelPropertyDescriptor(object);
      addIconURIPropertyDescriptor(object);
      addTooltipPropertyDescriptor(object);
      addLocalizedLabelPropertyDescriptor(object);
      addLocalizedTooltipPropertyDescriptor(object);
      addContextPropertyDescriptor(object);
      addVariablesPropertyDescriptor(object);
      addElementIdPropertyDescriptor(object);
      addTagsPropertyDescriptor(object);
      addContributorURIPropertyDescriptor(object);
      addTransientDataPropertyDescriptor(object);
      addContributionURIPropertyDescriptor(object);
      addObjectPropertyDescriptor(object);
      addToBeRenderedPropertyDescriptor(object);
      addOnTopPropertyDescriptor(object);
      addVisiblePropertyDescriptor(object);
      addContainerDataPropertyDescriptor(object);
      addCurSharedRefPropertyDescriptor(object);
      addAccessibilityPhrasePropertyDescriptor(object);
      addLocalizedAccessibilityPhrasePropertyDescriptor(object);
      addSelectedElementPropertyDescriptor(object);
      addMediaTypesPropertyDescriptor(object);
      addEditablePropertyDescriptor(object);
      addContributionsPropertyDescriptor(object);
      addPopupMenuPropertyDescriptor(object);
      addTransferFormatsPropertyDescriptor(object);
    }
    return itemPropertyDescriptors;
  }

  /**
   * This adds a property descriptor for the Label feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  protected void addLabelPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UILabel_label_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UILabel_label_feature", "_UI_UILabel_type"),
         UiPackageImpl.Literals.UI_LABEL__LABEL,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Icon URI feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  protected void addIconURIPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UILabel_iconURI_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UILabel_iconURI_feature", "_UI_UILabel_type"),
         UiPackageImpl.Literals.UI_LABEL__ICON_URI,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Tooltip feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  protected void addTooltipPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UILabel_tooltip_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UILabel_tooltip_feature", "_UI_UILabel_type"),
         UiPackageImpl.Literals.UI_LABEL__TOOLTIP,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Localized Label feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addLocalizedLabelPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UILabel_localizedLabel_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UILabel_localizedLabel_feature", "_UI_UILabel_type"),
         UiPackageImpl.Literals.UI_LABEL__LOCALIZED_LABEL,
         false,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Localized Tooltip feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addLocalizedTooltipPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UILabel_localizedTooltip_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UILabel_localizedTooltip_feature", "_UI_UILabel_type"),
         UiPackageImpl.Literals.UI_LABEL__LOCALIZED_TOOLTIP,
         false,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Context feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addContextPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Context_context_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Context_context_feature", "_UI_Context_type"),
         UiPackageImpl.Literals.CONTEXT__CONTEXT,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Variables feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addVariablesPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Context_variables_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Context_variables_feature", "_UI_Context_type"),
         UiPackageImpl.Literals.CONTEXT__VARIABLES,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Element Id feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addElementIdPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_ApplicationElement_elementId_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_ApplicationElement_elementId_feature", "_UI_ApplicationElement_type"),
         ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__ELEMENT_ID,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Tags feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTagsPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_ApplicationElement_tags_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_ApplicationElement_tags_feature", "_UI_ApplicationElement_type"),
         ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__TAGS,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Contributor URI feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addContributorURIPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_ApplicationElement_contributorURI_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_ApplicationElement_contributorURI_feature", "_UI_ApplicationElement_type"),
         ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__CONTRIBUTOR_URI,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Transient Data feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTransientDataPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_ApplicationElement_transientData_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_ApplicationElement_transientData_feature", "_UI_ApplicationElement_type"),
         ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__TRANSIENT_DATA,
         true,
         false,
         false,
         null,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Contribution URI feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @since 1.0
   * @generated
   */
  protected void addContributionURIPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Contribution_contributionURI_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Contribution_contributionURI_feature", "_UI_Contribution_type"),
         ApplicationPackageImpl.Literals.CONTRIBUTION__CONTRIBUTION_URI,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Object feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addObjectPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Contribution_object_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Contribution_object_feature", "_UI_Contribution_type"),
         ApplicationPackageImpl.Literals.CONTRIBUTION__OBJECT,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the To Be Rendered feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addToBeRenderedPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_toBeRendered_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_toBeRendered_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__TO_BE_RENDERED,
         true,
         false,
         false,
         ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the On Top feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addOnTopPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_onTop_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_onTop_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__ON_TOP,
         true,
         false,
         false,
         ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Visible feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addVisiblePropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_visible_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_visible_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__VISIBLE,
         true,
         false,
         false,
         ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Container Data feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addContainerDataPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_containerData_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_containerData_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__CONTAINER_DATA,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Cur Shared Ref feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addCurSharedRefPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_curSharedRef_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_curSharedRef_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__CUR_SHARED_REF,
         true,
         false,
         true,
         null,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Accessibility Phrase feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addAccessibilityPhrasePropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_accessibilityPhrase_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_accessibilityPhrase_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__ACCESSIBILITY_PHRASE,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Localized Accessibility Phrase feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addLocalizedAccessibilityPhrasePropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_UIElement_localizedAccessibilityPhrase_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_UIElement_localizedAccessibilityPhrase_feature", "_UI_UIElement_type"),
         UiPackageImpl.Literals.UI_ELEMENT__LOCALIZED_ACCESSIBILITY_PHRASE,
         false,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Selected Element feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addSelectedElementPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_ElementContainer_selectedElement_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_ElementContainer_selectedElement_feature", "_UI_ElementContainer_type"),
         UiPackageImpl.Literals.ELEMENT_CONTAINER__SELECTED_ELEMENT,
         true,
         false,
         true,
         null,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Media Types feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addMediaTypesPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Cell_mediaTypes_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Cell_mediaTypes_feature", "_UI_Cell_type"),
         uk.co.saiman.eclipse.model.ui.Package.Literals.CELL__MEDIA_TYPES,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Editable feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addEditablePropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Cell_editable_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Cell_editable_feature", "_UI_Cell_type"),
         uk.co.saiman.eclipse.model.ui.Package.Literals.CELL__EDITABLE,
         true,
         false,
         false,
         ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Contributions feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addContributionsPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Cell_contributions_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Cell_contributions_feature", "_UI_Cell_type"),
         uk.co.saiman.eclipse.model.ui.Package.Literals.CELL__CONTRIBUTIONS,
         true,
         false,
         true,
         null,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Popup Menu feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addPopupMenuPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Cell_popupMenu_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Cell_popupMenu_feature", "_UI_Cell_type"),
         uk.co.saiman.eclipse.model.ui.Package.Literals.CELL__POPUP_MENU,
         true,
         false,
         true,
         null,
         null,
         null));
  }

  /**
   * This adds a property descriptor for the Transfer Formats feature.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void addTransferFormatsPropertyDescriptor(Object object) {
    itemPropertyDescriptors.add
      (createItemPropertyDescriptor
        (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
         getResourceLocator(),
         getString("_UI_Cell_transferFormats_feature"),
         getString("_UI_PropertyDescriptor_description", "_UI_Cell_transferFormats_feature", "_UI_Cell_type"),
         uk.co.saiman.eclipse.model.ui.Package.Literals.CELL__TRANSFER_FORMATS,
         true,
         false,
         false,
         ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
         null,
         null));
  }

  /**
   * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
   * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
   * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
    if (childrenFeatures == null) {
      super.getChildrenFeatures(object);
      childrenFeatures.add(UiPackageImpl.Literals.CONTEXT__PROPERTIES);
      childrenFeatures.add(ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__PERSISTED_STATE);
      childrenFeatures.add(UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN);
      childrenFeatures.add(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);
      childrenFeatures.add(uk.co.saiman.eclipse.model.ui.Package.Literals.CELL__CONTRIBUTIONS);
    }
    return childrenFeatures;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EStructuralFeature getChildFeature(Object object, Object child) {
    // Check the type of the specified child object and return the proper feature to use for
    // adding (see {@link AddCommand}) it as a child.

    return super.getChildFeature(object, child);
  }

  /**
   * This returns Cell.gif.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getImage(Object object) {
    return overlayImage(object, getResourceLocator().getImage("full/obj16/Cell"));
  }

  /**
   * This returns the label text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText(Object object) {
    String label = ((Cell)object).getLabel();
    return label == null || label.length() == 0 ?
      getString("_UI_Cell_type") :
      getString("_UI_Cell_type") + " " + label;
  }


  /**
   * This handles model notifications by calling {@link #updateChildren} to update any cached
   * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void notifyChanged(Notification notification) {
    updateChildren(notification);

    switch (notification.getFeatureID(Cell.class)) {
      case uk.co.saiman.eclipse.model.ui.Package.CELL__LABEL:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__ICON_URI:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__TOOLTIP:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__LOCALIZED_LABEL:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__LOCALIZED_TOOLTIP:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__CONTEXT:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__VARIABLES:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__ELEMENT_ID:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__TAGS:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__CONTRIBUTOR_URI:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__TRANSIENT_DATA:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__CONTRIBUTION_URI:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__OBJECT:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__WIDGET:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__RENDERER:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__TO_BE_RENDERED:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__ON_TOP:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__VISIBLE:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__CONTAINER_DATA:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__ACCESSIBILITY_PHRASE:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__LOCALIZED_ACCESSIBILITY_PHRASE:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__MEDIA_TYPES:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__EDITABLE:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__TRANSFER_FORMATS:
        fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
        return;
      case uk.co.saiman.eclipse.model.ui.Package.CELL__PROPERTIES:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__PERSISTED_STATE:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__VISIBLE_WHEN:
      case uk.co.saiman.eclipse.model.ui.Package.CELL__CHILDREN:
        fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
        return;
    }
    super.notifyChanged(notification);
  }

  /**
   * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
   * that can be created under this object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
    super.collectNewChildDescriptors(newChildDescriptors, object);

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.CONTEXT__PROPERTIES,
         ((EFactory)MApplicationFactory.INSTANCE).create(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP)));

    newChildDescriptors.add
      (createChildParameter
        (ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__PERSISTED_STATE,
         ((EFactory)MApplicationFactory.INSTANCE).create(ApplicationPackageImpl.Literals.STRING_TO_STRING_MAP)));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN,
         MUiFactory.INSTANCE.createCoreExpression()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.UI_ELEMENT__VISIBLE_WHEN,
         MUiFactory.INSTANCE.createImperativeExpression()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         Factory.eINSTANCE.createCell()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         Factory.eINSTANCE.createTree()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         Factory.eINSTANCE.createHandledCell()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MApplicationFactory.INSTANCE.createApplication()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createMenuSeparator()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createMenu()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createMenuContribution()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createPopupMenu()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createDirectMenuItem()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createHandledMenuItem()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createToolBar()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createToolControl()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createHandledToolItem()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createDirectToolItem()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createToolBarSeparator()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createToolBarContribution()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createTrimContribution()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MMenuFactory.INSTANCE.createDynamicMenuContribution()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createPart()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createCompositePart()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createInputPart()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createPartStack()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createPartSashContainer()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createWindow()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createTrimmedWindow()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createTrimBar()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createDialog()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MBasicFactory.INSTANCE.createWizardDialog()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MAdvancedFactory.INSTANCE.createPlaceholder()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MAdvancedFactory.INSTANCE.createPerspective()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MAdvancedFactory.INSTANCE.createPerspectiveStack()));

    newChildDescriptors.add
      (createChildParameter
        (UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
         MAdvancedFactory.INSTANCE.createArea()));
  }

  /**
   * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getCreateChildText(Object owner, Object feature, Object child, Collection<?> selection) {
    Object childFeature = feature;
    Object childObject = child;

    boolean qualify =
      childFeature == UiPackageImpl.Literals.CONTEXT__PROPERTIES ||
      childFeature == ApplicationPackageImpl.Literals.APPLICATION_ELEMENT__PERSISTED_STATE;

    if (qualify) {
      return getString
        ("_UI_CreateChild_text2",
         new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
    }
    return super.getCreateChildText(owner, feature, child, selection);
  }

  /**
   * Return the resource locator for this item provider's resources.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ResourceLocator getResourceLocator() {
    return UISaimanEditPlugin.INSTANCE;
  }

}
