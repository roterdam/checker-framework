import java.util.*;

import checkers.util.test.Critical;

abstract class ThrowCatch {

  void throwsNoncritical() throws Exception {
    throw new Exception();
  }

  void throwsCritical() throws @Critical Exception {
    throw new @Critical Exception();
  }

  void catches() {
    try {
      throwsNoncritical();
    } catch (Exception e) {
    }

    try {
      throwsNoncritical();
    //:: (type.incompatible)
    } catch (@Critical Exception e) {
    }

    try {
      throwsCritical();
    } catch (Exception e) {
    }

    try {
      throwsCritical();
    } catch (@Critical Exception e) {
    }
  }

}
