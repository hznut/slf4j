package org.slf4j.ext.mdc.tree;

/**
 * @author Himanshu Vijay
 */
public class NumberProperty extends LeafNode<NumberProperty, Number> {
    public NumberProperty(String name, Node parent, Number defaultValue){
        super(name, NumberProperty.class, parent, Number.class, defaultValue);
    }

  @Override
  public NumberProperty copy(Node parent) {
    NumberProperty copy = new NumberProperty(this.NAME, null, this.DEFAULT_VALUE);
    copy.set(this.value);
    copy.setParent(parent);
    return copy;
  }
}
