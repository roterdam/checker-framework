package checkers.util.trees;

import javax.annotation.processing.ProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import checkers.util.InternalUtils;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

/**
 * The TreeBuilder permits the creation of new AST Trees using the
 * non-public Java compiler API TreeMaker.  Initially, it will support
 * construction of desugared Trees required by the CFGBuilder, e.g.
 * the pieces of a desugared enhanced for loop.
 */

public class TreeBuilder {
    private final Elements elements;
    private final Types types;
    private final TreeMaker maker;
    private final Names names;

    public TreeBuilder(ProcessingEnvironment env) {
        Context context = ((JavacProcessingEnvironment)env).getContext();
        elements = env.getElementUtils();
        types = env.getTypeUtils();
        maker = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    /**
     * Builds an AST Tree to access the iterator() method of some iterable
     * expression.
     *
     * @param iterableExpr  an expression whose type is a subtype of Iterable
     * @return  a MemberSelectTree that accesses the iterator() method of
     *    the expression
     */
    public MemberSelectTree buildIteratorMethodAccess(ExpressionTree iterableExpr) {
        DeclaredType exprType = (DeclaredType)InternalUtils.typeOf(iterableExpr);
        TypeElement exprElement = (TypeElement)exprType.asElement();

        assert exprType != null : "expression must be of declared type Iterable<>";

        // Find the iterator() method of the iterable type
        Symbol.MethodSymbol iteratorMethod = null;

        for (ExecutableElement method :
                 ElementFilter.methodsIn(elements.getAllMembers(exprElement))) {
            Name methodName = method.getSimpleName();

            if (method.getParameters().size() == 0) {
                if (methodName.contentEquals("iterator")) {
                    iteratorMethod = (Symbol.MethodSymbol)method;
                }
            }
        }

        assert iteratorMethod != null : "no iterator method declared for expression type";

        Type.MethodType methodType = (Type.MethodType)iteratorMethod.asType();
        Symbol.TypeSymbol methodClass = (Symbol.TypeSymbol)methodType.asElement();
        TypeMirror iteratorType = methodType.getReturnType();
        TypeMirror elementType = exprType.getTypeArguments().get(0);

        // Remove captured type from a wildcard.
        if (elementType instanceof Type.CapturedType) {
            elementType = ((Type.CapturedType)elementType).wildcard;
        }

        Type specificReturnType =
            (Type)types.getDeclaredType((TypeElement)types.asElement(iteratorType),
                                        elementType);

        // Replace the iterator method's generic return type with
        // the actual element type of the expression.
        Type.MethodType updatedMethodType =
            new Type.MethodType(com.sun.tools.javac.util.List.<Type>nil(),
                                specificReturnType,
                                com.sun.tools.javac.util.List.<Type>nil(),
                                methodClass);

        JCTree.JCFieldAccess iteratorAccess =
            (JCTree.JCFieldAccess)
            maker.Select((JCTree.JCExpression)iterableExpr,
                         iteratorMethod);
        iteratorAccess.setType(updatedMethodType);

        return iteratorAccess;
    }

    /**
     * Builds an AST Tree to access the hasNext() method of an iterator.
     *
     * @param iteratorExpr  an expression whose type is a subtype of Iterator
     * @return  a MemberSelectTree that accesses the hasNext() method of
     *    the expression
     */
    public MemberSelectTree buildHasNextMethodAccess(ExpressionTree iteratorExpr) {
        DeclaredType exprType = (DeclaredType)InternalUtils.typeOf(iteratorExpr);
        TypeElement exprElement = (TypeElement)exprType.asElement();

        assert exprType != null : "expression must be of declared type Iterator<>";

        // Find the hasNext() method of the iterator type
        Symbol.MethodSymbol hasNextMethod = null;

        for (ExecutableElement method :
                 ElementFilter.methodsIn(elements.getAllMembers(exprElement))) {
            Name methodName = method.getSimpleName();

            if (method.getParameters().size() == 0) {
                if (methodName.contentEquals("hasNext")) {
                    hasNextMethod = (Symbol.MethodSymbol)method;
                }
            }
        }

        assert hasNextMethod != null : "no hasNext method declared for expression type";

        JCTree.JCFieldAccess hasNextAccess =
            (JCTree.JCFieldAccess)
            maker.Select((JCTree.JCExpression)iteratorExpr,
                         hasNextMethod);
        hasNextAccess.setType((Type.MethodType)hasNextMethod.asType());

        return hasNextAccess;
    }

    /**
     * Builds an AST Tree to access the next() method of an iterator.
     *
     * @param iteratorExpr  an expression whose type is a subtype of Iterator
     * @return  a MemberSelectTree that accesses the next() method of
     *    the expression
     */
    public MemberSelectTree buildNextMethodAccess(ExpressionTree iteratorExpr) {
        DeclaredType exprType = (DeclaredType)InternalUtils.typeOf(iteratorExpr);
        TypeElement exprElement = (TypeElement)exprType.asElement();

        assert exprType != null : "expression must be of declared type Iterator<>";

        // Find the next() method of the iterator type
        Symbol.MethodSymbol nextMethod = null;

        for (ExecutableElement method :
                 ElementFilter.methodsIn(elements.getAllMembers(exprElement))) {
            Name methodName = method.getSimpleName();

            if (method.getParameters().size() == 0) {
                if (methodName.contentEquals("next")) {
                    nextMethod = (Symbol.MethodSymbol)method;
                }
            }
        }

        assert nextMethod != null : "no next method declared for expression type";

        Type.MethodType methodType = (Type.MethodType)nextMethod.asType();
        Symbol.TypeSymbol methodClass = (Symbol.TypeSymbol)methodType.asElement();
        Type specificReturnType = (Type)exprType.getTypeArguments().get(0);

        // Replace the next method's generic return type with
        // the actual element type of the expression.
        Type.MethodType updatedMethodType =
            new Type.MethodType(com.sun.tools.javac.util.List.<Type>nil(),
                                specificReturnType,
                                com.sun.tools.javac.util.List.<Type>nil(),
                                methodClass);

        JCTree.JCFieldAccess nextAccess =
            (JCTree.JCFieldAccess)
            maker.Select((JCTree.JCExpression)iteratorExpr,
                         nextMethod);
        nextAccess.setType(updatedMethodType);

        return nextAccess;
    }

    /**
     * Builds an AST Tree to call a method designated by the argument expression.
     *
     * @param methodExpr  an expression denoting a method with no arguments
     * @return  a MethodInvocationTree to call the argument method
     */
    public MethodInvocationTree buildMethodInvocation(ExpressionTree methodExpr) {
        return maker.App((JCTree.JCExpression)methodExpr);
    }

    /**
     * Builds an AST Tree to declare and initialize a variable, with no modifiers.
     *
     * @param type  the type of the variable
     * @param name  the name of the variable
     * @param initializer  the initializer expression
     * @return  a VariableDeclTree declaring the new variable
     */
    public VariableTree buildVariableDecl(DeclaredType type,
                                          String name,
                                          Element owner,
                                          ExpressionTree initializer) {
        Symbol.VarSymbol sym =
            new Symbol.VarSymbol(0, names.fromString(name),
                                 (Type.ClassType)type, (Symbol)owner);
        return maker.VarDef(sym, (JCTree.JCExpression)initializer);
    }

    /**
     * Builds an AST Tree to refer to a variable.
     *
     * @param decl  the declaration of the variable
     * @return  an IdentifierTree to refer to the variable
     */
    public IdentifierTree buildVariableUse(VariableTree decl) {
        return (IdentifierTree)maker.Ident((JCTree.JCVariableDecl)decl);
    }

    /**
     * Builds an AST Tree to cast the type of an expression.
     *
     * @param type  the type to cast to
     * @param expr  the expression to be cast
     * @return  a cast of the expression to the type
     */
    public TypeCastTree buildTypeCast(TypeMirror type,
                                      ExpressionTree expr) {
        return maker.TypeCast((Type)type, (JCTree.JCExpression)expr);
    }
    
    /**
     * Builds an AST Tree to assign an expression to a variable.
     *
     * @param variable  the declaration of the variable to assign to
     * @param expr      the expression to be assigned
     * @return  a statement assigning the expression to the variable
     */
    public StatementTree buildAssignment(VariableTree variable,
                                         ExpressionTree expr) {
        return maker.Assignment(TreeInfo.symbolFor((JCTree)variable),
                                (JCTree.JCExpression)expr);
    }
}




