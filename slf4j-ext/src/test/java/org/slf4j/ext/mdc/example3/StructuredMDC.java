package org.slf4j.ext.mdc.example3;

import org.slf4j.ext.mdc.annotation.Pojo;
import org.slf4j.ext.mdc.annotation.Property;
import org.slf4j.ext.mdc.annotation.RootPojo;
import org.slf4j.ext.mdc.tree.*;

/**
 * Created by himavija on 12/15/15.
 */
@RootPojo(name = "event")
public class StructuredMDC extends RootNode<StructuredMDC> {
//  static{
//    staticInit(StructuredMDC.class);
//  }

  private StructuredMDC(){
    super("EVENT", StructuredMDC.class, "event");//TODO: how to check body of constructor during compilation/annotation processing ??
  }

//  protected static RootNode newInstance() {
    //return new StructuredMDC();
//    return this("EVENT", StructuredMDC.class, "event");
//  }

//  private static final InheritableThreadLocal<StructuredMDC> holder = new InheritableThreadLocal<StructuredMDC>() {
//    @Override
//    protected StructuredMDC initialValue() {
//      return new StructuredMDC();
//    }
//
//    @Override
//    protected StructuredMDC childValue(StructuredMDC parentThreadValue) {
//      if(parentThreadValue != null) {
//        return (StructuredMDC) parentThreadValue.copy();
//      }
//      return new StructuredMDC();
//    }
//  };

  private static final GenericRootNodeHolder<StructuredMDC> holder = new GenericRootNodeHolder<StructuredMDC>(StructuredMDC.class);

  public static StructuredMDC get() {
    return holder.get();//TODO: how to check for this body of method during compilation/annotation processing ??
  }

  @Property(name="who")
  public final Who who = new Who("who", this);

  @Property(name="what")
  public final What what = new What("what", this);

  @Property(name="trackingId")
  public final StringProperty trackingId = new StringProperty("trackingId", this, "0");

  @Pojo(name = "who")
  public static class Who extends NonLeafNode<Who> {

    private Who(String name, Node parent){
      super(name, Who.class, parent);
    }

    @Property(name="id")
    public final StringProperty id = new StringProperty("id", this, "");
  }

  @Pojo(name = "what")
  public static class What extends NonLeafNode<What> {

    private What(String name, Node parent){
      super(name, What.class, parent);
    }

    @Property(name="outcome")
    public final Outcome outcome = new Outcome("outcome", this);

    @Pojo(name="Outcome")
    public static class Outcome extends NonLeafNode<Outcome> {

      private Outcome(String name, Node parent){
        super(name, Outcome.class, parent);
      }

      @Property(name="result_string")
      public final StringProperty resultString = new StringProperty("result_string", this, "");

      @Property(name="result_number")
      public final NumberProperty resultNumber = new NumberProperty("result_number", this, 0);

      @Property(name="result_boolean")
      public final BooleanProperty resultBoolean = new BooleanProperty("result_boolean", this, false);

//      @Property(name="result_object")
//      private Object resultObject;

//      @Property(name="result_array")
//      private List resultArray;

//      @Property(name="result_map")
//      private Map resultMap;

      @Property(name="error")
      public final StringProperty error = new StringProperty("error", this, "");

      @Property(name="statusCode")
      public final IntegerProperty statusCode = new IntegerProperty("statusCode", this, 0);
    }
  }

}
