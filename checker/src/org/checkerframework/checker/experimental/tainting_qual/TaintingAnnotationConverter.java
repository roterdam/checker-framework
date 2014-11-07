package org.checkerframework.checker.experimental.tainting_qual;

import org.checkerframework.checker.experimental.tainting_qual.qual.Tainted;
import org.checkerframework.checker.experimental.tainting_qual.qual.Untainted;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.qualframework.base.AnnotationConverter;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;


public class TaintingAnnotationConverter implements AnnotationConverter<Tainting> {
    private Tainting fromAnnotation(AnnotationMirror anno) {
        if (AnnotationUtils.areSameByClass(anno, Tainted.class)) {
            return Tainting.TAINTED;
        } else if (AnnotationUtils.areSameByClass(anno, Untainted.class)) {
            return Tainting.UNTAINTED;
        }
        return null;
    }

    @Override
    public Tainting fromAnnotations(Collection<? extends AnnotationMirror> annos) {
        for (AnnotationMirror anno : annos) {
            Tainting result = fromAnnotation(anno);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean isAnnotationSupported(AnnotationMirror anno) {
        return fromAnnotation(anno) != null;
    }
}

