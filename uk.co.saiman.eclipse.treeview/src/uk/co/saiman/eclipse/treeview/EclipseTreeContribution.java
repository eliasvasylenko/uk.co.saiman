package uk.co.saiman.eclipse.treeview;

import java.util.stream.Stream;

import javafx.scene.Node;
import uk.co.saiman.fx.TreeCellContribution;
import uk.co.saiman.fx.TreeChildContribution;
import uk.co.saiman.fx.TreeItemData;
import uk.co.saiman.fx.TreeTextContribution;
import uk.co.saiman.reflection.token.TypedReference;

public class EclipseTreeContribution implements TreeChildContribution<Object>,
    TreeCellContribution<Object>, TreeTextContribution<Object> {
  @Override
  public <U> boolean appliesTo(TreeItemData<U> data) {
    // TODO Auto-generated method stub
    return TreeChildContribution.super.appliesTo(data);
  }
  
  @Override
  public <U> String getText(TreeItemData<U> item) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U> String getSupplementalText(TreeItemData<U> item) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U> Node configureCell(TreeItemData<U> item, Node content) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <U> Stream<TypedReference<?>> getChildren(TreeItemData<U> data) {
    // TODO Auto-generated method stub
    return null;
  }
}
