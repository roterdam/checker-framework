package checkers.units.quals;

import java.lang.annotation.*;

import checkers.quals.*;

/**
 * Units of acceleration.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TypeQualifier
@SubtypeOf(Unqualified.class)
public @interface Acceleration {}
