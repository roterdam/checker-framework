public class UnboxConditions {
    public static void main(String[] args) {
        Boolean b = null;
        Boolean b1 = null;
        Boolean b2 = null;
        Boolean b3 = null;
        Boolean b4 = null;
        //:: error: (condition.nullable)
        if (b) { ; }
        //:: error: (condition.nullable)
        b = b1 ? b : b;
        //:: error: (condition.nullable)
        while (b2) { ; }
        //:: error: (condition.nullable)
        do { ; } while (b3);
        //:: error: (condition.nullable)
        for (;b4;) { ; }
        // legal!
        for (;;) { break; }
        // Why is eluding the condition in a "for" legal, but not in a "while"?
        // while () {}
    }
}
