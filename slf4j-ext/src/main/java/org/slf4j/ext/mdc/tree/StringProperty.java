package org.slf4j.ext.mdc.tree;

/**
 * @author Himanshu Vijay
 */
public class StringProperty extends LeafNode<StringProperty, String> {
  public StringProperty(String name, Node parent, String defaultValue) {
    super(name, StringProperty.class, parent, String.class, defaultValue);
  }

  @Override
  public StringProperty copy(Node parent) {
    StringProperty copy = new StringProperty(this.NAME, null, this.DEFAULT_VALUE);
    copy.set(this.value);
    copy.setParent(parent);
    return copy;
  }
}
