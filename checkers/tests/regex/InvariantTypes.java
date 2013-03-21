import java.util.*;
import checkers.regex.quals.*;

public class InvariantTypes {
  String[] sa = {"a"};
  String[] sa2 = {"a", "b"};
  public String[] sa3 = {"a", "b"};
  public static  String[] sa4 = {"a", "b"};
  public final String[] sa5 = {"a", "b"};
  public static final String[] sa6 = {"a", "b"};
  final String[] sa7 = {"a", "b"};

  // tested above:  String[] sa = {"a"};
  @Regex String[] rsa = {"a"};
  String[] nrsa = {"(a"};
  //:: error: (array.initializer.type.incompatible) :: error: (assignment.type.incompatible)
  @Regex String[] rsaerr = {"(a"};

  List<String> ls = Arrays.asList("alice", "bob", "carol");
  List<@Regex String> lrs = Arrays.asList("alice", "bob", "carol");
  List<String> lnrs = Arrays.asList("(alice", "bob", "carol");
  //:: error: (assignment.type.incompatible)
  List<@Regex String> lrserr = Arrays.asList("(alice", "bob", "carol");

  void unqm(String[] sa) {}
  void rem(@Regex String[] rsa) {}

  void recalls() {
    unqm(new String[] {"a"});
    //TODOINVARR:: error: (argument.type.incompatible)
    unqm(new @Regex String[] {"a"});
    rem(new String[] {"a"});
    rem(new @Regex String[] {"a"});
  }

  void unqcalls() {
    unqm(new String[] {"a("});
    //TODOINVARR:: error: (argument.type.incompatible)
    //:: error: (array.initializer.type.incompatible)
    unqm(new @Regex String[] {"a("});
    //:: error: (argument.type.incompatible)
    rem(new String[] {"a("});
    //:: error: (array.initializer.type.incompatible)
    rem(new @Regex String[] {"a("});
  }

  // method argument context

    String[] retunqm(String[] sa) { return sa;}
    @Regex String[] retrem(@Regex String[] rsa) { return rsa; }
    @Regex String[] mixedm( String[] rsa) { return null; }

    void retunqcalls() {
        @Regex String[] re = mixedm(new String[] {"a("});
        //TODOINVARR:: error: (argument.type.incompatible)
        String [] u = retunqm(new String[] {"a"});
        //TODOINVARR:: error: (argument.type.incompatible)
        re = mixedm(new String[2]);
    }
}
