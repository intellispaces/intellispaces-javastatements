package intellispaces.common.javastatement.samples;

import intellispaces.common.javastatement.support.TesteeType;

public interface InterfaceExtendedTwoInterfaces {

  @TesteeType
  interface TesteeInterface extends Interface1, Interface2 {
  }

  interface Interface1 {
  }

  interface Interface2 {
  }
}
