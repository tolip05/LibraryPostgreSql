package com.cc.annotation.processor;

import com.cc.annotation.CCBatchUpdate;
import com.cc.annotation.CCUpdate;
import com.cc.annotation.NoParameters;
import com.cc.annotation.Parameters;
import com.cc.logging.LOG;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

@SupportedAnnotationTypes({ "com.cc.annotation.CCUpdate",
        "com.cc.annotation.CCBatchUpdate" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CCUpdateProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<? extends Element> entityAnnotatedWithCCUpdate = roundEnv.getElementsAnnotatedWith(CCUpdate.class);
        Set<? extends Element> entityAnnotatedWithCCBatchUpdate = roundEnv.getElementsAnnotatedWith(CCBatchUpdate.class);
        // process CCUpdate
        for (TypeElement typeElement : ElementFilter.typesIn(entityAnnotatedWithCCUpdate)) {
            process(typeElement,
                    CCUpdate.class);
        }
        // process CCBatchUpdate
        for (TypeElement typeElement : ElementFilter.typesIn(entityAnnotatedWithCCBatchUpdate)) {
            process(typeElement,
                    CCBatchUpdate.class);
        }
        return false;
    }

    private void process(TypeElement typeElement,
                         Class<? extends Annotation> annotationClass) {
        BuilderUtil builderUtil = new BuilderUtil(processingEnv, annotationClass);
        String sqlString = builderUtil.findSqlStringConstant(typeElement);
        if (sqlString != null)
            if (builderUtil.checkForPojoParamsAnnotations(typeElement)) {
                try {
                    if (typeElement.getAnnotation(NoParameters.class) != null) {
                        // No params
                        if (builderUtil.checkForParamsInSelect(typeElement,
                                sqlString))
                            builderUtil.writeBuilder(typeElement,
                                    null,
                                    sqlString);
                    }
                    if (typeElement.getAnnotation(Parameters.class) != null) {
                        // With params
                        Parameters parameters = typeElement.getAnnotation(Parameters.class);
                        if (builderUtil.checkParametersAnnotation(typeElement,
                                parameters))
                            if (builderUtil.checkForSelectParametersCount(typeElement,
                                    parameters,
                                    sqlString))
                                builderUtil.writeBuilder(typeElement,
                                        parameters,
                                        sqlString);
                    }
                } catch (IOException e) {
                    LOG.getLog(getClass())
                            .error(e);
                }
            }
    }
}
