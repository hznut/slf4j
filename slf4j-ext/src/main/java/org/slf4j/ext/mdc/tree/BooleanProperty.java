package org.slf4j.ext.mdc.tree;

/**
 * @author Himanshu Vijay
 */
public class BooleanProperty extends LeafNode<BooleanProperty, Boolean> {
    public BooleanProperty(String name, Node parent, Boolean defaultValue){
        super(name, BooleanProperty.class, parent, Boolean.class, defaultValue);
    }

  @Override
  public BooleanProperty copy(Node parent) {
    BooleanProperty copy = new BooleanProperty(this.NAME, null, this.DEFAULT_VALUE);
    copy.set(this.value);
    copy.setParent(parent);
    return copy;
  }
}
