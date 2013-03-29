package checkers.nullness.quals;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import checkers.nullness.NullnessRawnessChecker;
import checkers.quals.SubtypeOf;
import checkers.quals.TypeQualifier;

/**
 * This type qualifier belongs to the rawness initialization tracking
 * type-system. This type-system is not used on its own, but in conjunction with
 * some other type-system that wants to ensure safe initialization. For
 * instance, {@link NullnessRawnessChecker} uses rawness to track initialization
 * of {@link NonNull} fields.
 *
 * <p>
 * This type qualifier indicates that the object might not have been fully
 * initialized, where "being initialized" refers to the state of fields and
 * whether they have a value that corresponds to their type qualifier. What type
 * qualifier are considered depends on the checker; for instance, the
 * {@link NullnessRawnessChecker} considers {@link NonNull}.
 *
 * <p>
 * Therefore, reading fields of an object of type {@link Raw} might yield
 * objects that do not correspond to the type qualifier written for that field.
 * For instance, in the {@link NullnessRawnessChecker}, fields might be
 * {@code null} even if they have been annotated as {@link NonNull}.
 *
 * <p>
 * More precisely, an expression of type {@code @Raw(T.class)} refers to an
 * object that has all fields of {@code T} (and any super-classes) initialized
 * (e.g. to a non-null value in the {@link NullnessRawnessChecker}).
 *
 * <p>
 * At the beginning of a constructor, the fields of the object are not yet
 * initialized and thus {@link Raw} is used as the type of the self-reference
 * {@code this}. Consider a class {@code B} that is a subtype of {@code A}. At
 * the beginning of the constructor of {@code B}, {@code this} has the type
 * {@code @Raw(A.class)}, since all fields of {@code A} have been initialized by
 * the super-constructor. If during the constructor also all fields of {@code B}
 * are initialized, then the type of {@code this} changes to
 * {@code @Raw(B.class)} (and otherwise, if not all fields are initialized, an
 * error is issued).
 *
 * <p>
 * Note that it would not be sound to type {@code this} as {@link NonRaw}
 * anywhere in a constructor (with the exception of final classes; but this is
 * currently not implemented), because there might always be subclasses with
 * uninitialized fields. The following example shows why:
 *
 * <pre>
 * <code>
 *   class A {
 *      @NonNull String a;
 *      public A() {
 *          a = "";
 *          // Now, all fields of A are initialized.
 *          // However, if this constuctor is invoked as part of 'new B()', then
 *          // the fields of B are not yet initialized.
 *          // If we would type 'this' as @NonRaw, then the following call is valid:
 *          foo();
 *      }
 *      void foo() {}
 *   }
 *
 *   class B extends A {
 *      @NonNull String b;
 *      @Override
 *      void foo() {
 *          // Dereferencing 'b' is ok, sind 'this' is @NonRaw and 'b' @NonNull.
 *          // However, in 'new B()', this throws a null-pointer exception.
 *          b.toString();
 *      }
 *   }
 * </code>
 * </pre>
 */
@Documented
@SubtypeOf({})
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE, ElementType.TYPE_PARAMETER })
public @interface Raw {
    /**
     * The type-frame down to which the expression (of this type) has been
     * initialized at least (inclusive). That is, an expression of type
     * {@code @Raw(T.class)} has all type-frames initialized starting at
     * {@code Object} down to (and including) {@code T}.
     */
    Class<?> value() default Object.class;
}
