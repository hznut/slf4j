package org.slf4j.ext.mdc.tree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by himavija on 12/18/15.
 */
public class GenericRootNodeHolder<R extends RootNode<R>> extends InheritableThreadLocal<R> {
  private final Class<R> clazz;
  private Constructor<R> constructor = null;

  public GenericRootNodeHolder(Class<R> clazz){
    this.clazz = clazz;
    try {
      //We know and enforce that RootNode child classes must have a private no-args constructor
      constructor = clazz.getDeclaredConstructor();//Optimization so that we don't execute this repeatedly in instantiateR()
      constructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      //Should never happen
    }
  }

  @Override
  protected R initialValue() {
    return instantiateR();
  }

  @Override
  protected R childValue(R parentThreadValue) {
    if(parentThreadValue != null) {
      return parentThreadValue.copy();
    }
    return instantiateR();
  }

  private R instantiateR(){
    try {
      return constructor.newInstance();
    } catch (InvocationTargetException e) {
      return null;//Should not happen unless a special SecurityManager is present
    } catch (InstantiationException e) {
      return null;//Should not happen unless a special SecurityManager is present
    } catch (IllegalAccessException e) {
      return null;//Should not happen unless a special SecurityManager is present
    }
  }
}
