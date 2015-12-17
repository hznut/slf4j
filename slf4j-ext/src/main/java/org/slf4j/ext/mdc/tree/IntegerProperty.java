package org.slf4j.ext.mdc.tree;

/**
 * @author Himanshu Vijay
 */
public class IntegerProperty extends LeafNode<IntegerProperty, Integer> {
    public IntegerProperty(String name, Node parent, Integer defaultValue){
        super(name, IntegerProperty.class, parent, Integer.class, defaultValue);
    }

  @Override
  public IntegerProperty copy(Node parent) {
    IntegerProperty copy = new IntegerProperty(this.NAME, null, this.DEFAULT_VALUE);
    copy.set(this.value);
    copy.setParent(parent);
    return copy;
  }
}
