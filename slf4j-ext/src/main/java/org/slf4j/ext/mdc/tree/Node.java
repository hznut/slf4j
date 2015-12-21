package org.slf4j.ext.mdc.tree;

/**
 * @author Himanshu Vijay
 */
public abstract class Node<T extends Node> implements Cloneable {
  private final Class<T> clazz;
  protected final String NAME;
  protected final String FQN;
  protected final String SEPARATOR_FOR_FQN;
  public static final String DEFAULT_SEPARATOR_FOR_FQN = ".";
  private Node parent;

  Node(String name, Class<T> clazz, Node parent, String separatorForFqn) {
    this.clazz = clazz;
    this.NAME = name;
    this.parent = parent;
    if(this.parent != null) {
      this.SEPARATOR_FOR_FQN = parent.SEPARATOR_FOR_FQN;
      this.FQN = String.format("%s%s%s", this.parent.FQN, SEPARATOR_FOR_FQN, NAME);
    } else {
      this.SEPARATOR_FOR_FQN = separatorForFqn;
      this.FQN = NAME;
    }
  }

  protected Node(String name, Class<T> clazz, Node parent) {
    this(name, clazz, parent, (parent == null) ? DEFAULT_SEPARATOR_FOR_FQN : parent.SEPARATOR_FOR_FQN);
  }

//  protected void setParent(Node parent) {
//    this.parent = parent;
//  }

  public abstract void setToDefault();

  public T copy() {
    return copy(null);
  }

  /**
   * <b>Why copy(..) method when we can override clone() ?</b>
   * clone() method's return type is Object whereas stricter type safety is desired overall for the inner workings of this API.
   * Hence the copy() method(s). clone() simply calls copy().
   *
   * @param parent
   * @return
   */
  protected abstract T copy(Node parent);

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return copy();
  }
}
