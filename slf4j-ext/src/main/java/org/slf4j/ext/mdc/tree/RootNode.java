package org.slf4j.ext.mdc.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Root of the POJO tree which is used to capture event information. The tree must be clonable. This is required for it's internal workings.
 * Simple way of making tree clonable is to make each node clonable.
 * <p/>
 * Recommendations for POJO tree:
 * 1.
 * <p/>
 * Recommendations on usage:
 * 1. Do not use this API for storing sessions, metrics etc. Use it only for the intended purpose - capture event information
 * and log it.
 * 2. No guarantees on behavior of grandchild(ren) of this class. It has been tested only for direct subclasses.
 *
 * @author Himanshu Vijay
 */
public abstract class RootNode<R extends RootNode> extends NonLeafNode<R> {
  private final Class<R> clazz;
  public final String EVENT_MARKER_STRING;
  private final Marker MARKER;
  private static final Logger LOGGER = LoggerFactory.getLogger(RootNode.class);

  /*So that multiple types of RootNode implementations can co-exist in same JVM*/
//  private static final Map<Class, GenericRootNodeHolder> holderLookup = new HashMap<Class, GenericRootNodeHolder>();
//
//  protected static <T extends RootNode> void staticInit(Class<T> rootNodeRuntimeClass){
//    holderLookup.put(rootNodeRuntimeClass, new GenericRootNodeHolder<T>(rootNodeRuntimeClass));
//  }
//
//  public static <T extends RootNode> T get(){
//
//    Class<T> c = (Class<T>) new Object(){}.getClass().getEnclosingClass();//TODO Figure out how to get runtime class
//    return (T) holderLookup.get(c).get();
//  }

  /**
   * @param marker Could be something like 'EVENT' or 'AUDIT' etc.
   */
  protected RootNode(String marker, Class<R> type, String name, String separatorForFqn) {
    super(name, type, null, separatorForFqn);
    clazz = type;
    if(marker != null && !marker.isEmpty()) {
      EVENT_MARKER_STRING = marker;
      MARKER = MarkerFactory.getDetachedMarker(EVENT_MARKER_STRING);
    } else {
      EVENT_MARKER_STRING = null;
      MARKER = null;
    }
  }

  protected RootNode(Class<R> type, String name, String separatorForFqn) {
    this(null, type, name, separatorForFqn);
  }

  protected RootNode(Class<R> type, String name) {
    this(null, type, name, DEFAULT_SEPARATOR_FOR_FQN);
  }

  protected RootNode(String marker, Class<R> type, String name) {
    this(marker, type, name, DEFAULT_SEPARATOR_FOR_FQN);
  }

  /**
   * Calls copy(). Ignores the param 'parent' since there's no parent for root node.
   *
   * @param parent Ignored
   * @return
   */
  @Override
  public final R copy(Node parent) {
    return copy();
  }

  /**
   * Sends the json for this event to log4j log appenders with highest log level i.e. error and marker string 'EVENT'.
   * <p/>
   * Log level = Error is just to ensure that this record does get logged.
   * Log level does not matter here much b'se typically there's something similar inside the json/xml/key-value representation of event itself, something like 'event severity'.
   * <p/>
   * The marker string 'EVENT' is used by log4j2.xml / logback.xml / log4j.properties to distinguish between these records and the regular application logs.
   */
  public void logJson() {
    if(MARKER != null){
      LOGGER.error(MARKER, toJson());
    } else {
      LOGGER.error(toJson());
    }
  }

  public void logXml() {
    if(MARKER != null){
      LOGGER.error(MARKER, toXml());
    } else {
      LOGGER.error(toXml());
    }
  }

  /**
   * Log as comma separated list of key-value pairs.
   */
  public void logCSKV() {
    LOGGER.error(MARKER, toCSKV("=", ","));
  }

//  public static RootNode get() {
//    return (RootNode) holder.get();
//  }

  public void reset() {
    setToDefault();
  }
}
