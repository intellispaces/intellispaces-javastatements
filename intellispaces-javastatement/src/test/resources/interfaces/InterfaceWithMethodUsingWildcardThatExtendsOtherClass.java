package intellispaces.common.javastatement.samples;

import intellispaces.common.javastatement.support.TesteeType;

import java.util.Collection;

@TesteeType
public interface InterfaceWithMethodUsingWildcardThatExtendsOtherClass {

  void methodUsingWildcardThatExtendsOtherClass(Collection<? extends Number> arg);
}