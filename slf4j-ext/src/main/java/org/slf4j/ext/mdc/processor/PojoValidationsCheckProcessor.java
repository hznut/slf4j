package org.slf4j.ext.mdc.processor;


import com.google.auto.service.AutoService;
import org.slf4j.ext.mdc.annotation.Pojo;
import org.slf4j.ext.mdc.annotation.Property;
import org.slf4j.ext.mdc.annotation.RootPojo;
import org.slf4j.ext.mdc.tree.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class PojoValidationsCheckProcessor extends AbstractProcessor {

  private int level = 1;
  private Types typeUtils;
  private Elements elementUtils;
  private Filer filer;
  private Messager messsager;
  private Node rootNode;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    typeUtils = processingEnvironment.getTypeUtils();
    elementUtils = processingEnvironment.getElementUtils();
    filer = processingEnvironment.getFiler();
    messsager = processingEnvironment.getMessager();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<String>();
    annotations.add(Pojo.class.getCanonicalName());
    annotations.add(RootPojo.class.getCanonicalName());
    annotations.add(Property.class.getCanonicalName());
    return annotations;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
    Set rootElementSet = roundEnvironment.getElementsAnnotatedWith(RootPojo.class);
    TypeElement rootElement = (TypeElement) rootElementSet.iterator().next();
    try {
      checkValidations(rootElement);
    } catch (ProcessingException e) {
     error(e.getElement(), e.getMessage());
    }
    return true;
  }

  private void checkValidations(Element root) throws ProcessingException{
    if(isRootElement(root) &&
            isValidRoot(root) ){
      for(Element e: root.getEnclosedElements()) {
        hasPrivateConstructor(e);
        hasRoot(e);
        hasNoArgsConstructor(e);
        hasGetMethod(e);
        hasNewInstanceMethod(e);
        hasValidProperty(e);
        hasValidPojo(e);
      }
      System.out.println("All validations are passed !!!");
    } else {
      throw new ProcessingException(root, "Invalid annotation on the root node");
    }
  }

  private boolean hasRoot(Element e) throws ProcessingException{
    if(e.getAnnotation(RootPojo.class) != null){
      throw new ProcessingException(e, "There can be only one root in a POJO");
    }
    return true;
  }

  private boolean isValidRoot(Element root) throws ProcessingException{
    TypeMirror superTypeMirror = ((TypeElement)root).getSuperclass();
    if(!superTypeMirror.toString().equals(RootNode.class.getCanonicalName().toString()+"<"+root.toString()+">")){
      throw new ProcessingException(root, "The class annotated with RootPojo must extend RootNode class having its own type as generic");
    }
    return true;
  }

  private boolean hasPrivateConstructor(Element e) throws ProcessingException{
    if(e.getKind() == ElementKind.CONSTRUCTOR ){
      if(!e.getModifiers().contains(Modifier.PRIVATE)){
        throw new ProcessingException(e, "The constructor must be private");
      }
    }
    return true;
  }

  private boolean hasNoArgsConstructor(Element e) throws ProcessingException{
    if(e.getKind() == ElementKind.CONSTRUCTOR ){
      if(((ExecutableElement)e).getParameters().size() != 0){
        throw new ProcessingException(e, "No parameters should be there in Default Constructor ");
      }
    }
    return true;
  }

  private boolean hasGetMethod(Element e) throws ProcessingException{
    ExecutableElement methodElement;
    if(e.getKind() == ElementKind.METHOD ){
      methodElement = ((ExecutableElement)e);
      if(methodElement.getSimpleName().equals("get")){
        if(!methodElement.getModifiers().contains(Modifier.PUBLIC)) {
          throw new ProcessingException(methodElement, "The method get() must be public ");
        }
        if(!methodElement.getModifiers().contains(Modifier.STATIC)) {
          throw new ProcessingException(methodElement, "The method get() must be static ");
        }
        if(!methodElement.getReturnType().toString().equals(this.getClass().getSimpleName().toString())) {
          throw new ProcessingException(methodElement, "The return type of the method get() must be "+ this.getClass().toString());
        }
      }
    }
    return true;
  }

  private boolean hasNewInstanceMethod(Element e) throws ProcessingException{
    ExecutableElement methodElement;
    if(e.getKind() == ElementKind.METHOD ){
      methodElement = ((ExecutableElement)e);
      if(methodElement.getSimpleName().equals("newInstance")){
        if(!methodElement.getModifiers().contains(Modifier.PROTECTED)) {
          throw new ProcessingException(methodElement, "The method newInstance() must be protected");
        }
        if(!methodElement.getModifiers().contains(Modifier.STATIC)) {
          throw new ProcessingException(methodElement, "The method newInstance() must be static");
        }
        if(!methodElement.getReturnType().toString().equals(RootNode.class.getSimpleName().toString())) {
          throw new ProcessingException(methodElement, "The return type of the method newInstance() must be "+ RootNode.class.getSimpleName().toString());
        }
      }
    }
    return true;
  }

  private boolean hasValidProperty(Element e) throws ProcessingException {
    VariableElement propElement;
    TypeMirror superClassType;
    if(e.getAnnotation(Property.class) != null ){
      propElement = (VariableElement)e;
      if(!e.getModifiers().contains(Modifier.PUBLIC) ||
              !e.getModifiers().contains(Modifier.FINAL)){
        throw new ProcessingException(propElement, "The fields annotated with Property must be public final");
      }
      superClassType = ((TypeElement)(((DeclaredType) e.asType()).asElement())).getSuperclass();
      if(superClassType.toString().contains(LeafNode.class.getCanonicalName().toString())) {
        /*if(((VariableElement) e).getConstantValue() == null) {
          throw new ProcessingException(e, "The Node must be initialized with non-null value");
        }*/
        System.out.println("Leaf "+e);
      } else if(superClassType.toString().contains(NonLeafNode.class.getCanonicalName().toString())){
        System.out.println("NonLeaf "+ e);
      }
    }
    return true;
  }

  private boolean hasValidPojo(Element e) throws ProcessingException{
    VariableElement propElement;
      if(e.getAnnotation(Pojo.class) != null ){
        hasPrivateConstructor(e);
        hasArgsConstructor(e);
        hasCalltoSuperConstructor(e);
        isValidPojo(e);
      }
    return true;
  }

  private boolean isValidPojo(Element pojo) throws ProcessingException{
      TypeMirror superTypeMirror = ((TypeElement)pojo).getSuperclass();
      if(!superTypeMirror.toString().equals(NonLeafNode.class.getCanonicalName().toString()+"<"+pojo.toString()+">")){
        throw new ProcessingException(pojo, "The class annotated with Pojo must extend NonLeafNode class having its own type as generic ");
      }
      return true;
  }

  private boolean hasArgsConstructor(Element e) throws ProcessingException{
    List<? extends VariableElement> parametersList;
      if(e.getKind() == ElementKind.CONSTRUCTOR ){
        parametersList = ((ExecutableElement)e).getParameters();
        if(parametersList.size() != 2){
          throw new ProcessingException(e, "Two parameters should be there in the Constructor ");
        }
        if(!parametersList.get(0).asType().toString().equals("java.lang.String") ||
                !parametersList.get(1).asType().toString().equals("org.slf4j.ext.mdc.tree.Node")){
          throw new ProcessingException(e, "The Constructor must have 2 arguments of type String and Node ");
        }
      }
    return true;
  }

  //TODO: Find a way to get he list of statements
  private boolean hasCalltoSuperConstructor(Element e) throws ProcessingException{
    List<? extends Element> parametersList;
      if(e.getKind() == ElementKind.CONSTRUCTOR ){
        parametersList = ((ExecutableElement)e).getEnclosedElements();
        //System.out.println(parametersList);
      }
    return true;
  }

  private boolean isRootElement(Element rootElement) {
    if(rootElement.getAnnotation(RootPojo.class) != null) {
      return true;
    }
    return false;
  }

  private boolean isPojo(Element element) {
    if((element.getAnnotation(Pojo.class) != null)) {
      return true;
    }
    return false;
  }

  private boolean isPojoType(Element element) {
    if(((DeclaredType) (element.asType())).asElement().getAnnotation(Pojo.class) != null) {
      return true;
    }
    return false;
  }

  private boolean isProperty(Element element) {
    if(element.getAnnotation(Property.class) != null) {
      return true;
    }
    return false;
  }

  public void error(Element e, String msg) {
    System.out.println("");
    System.out.println("[["+Diagnostic.Kind.ERROR + "]] : "+msg +" in "+ e);
    System.out.println("");
  }
}
