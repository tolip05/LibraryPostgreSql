package com.cc.annotation.processor;

import com.cc.annotation.CCSelectExists;
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

@SupportedAnnotationTypes({ "com.cc.annotation.CCSelect",
        "com.cc.annotation.CCSelectExists" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CCSelectProcessor extends AbstractProcessor {
    @SuppressWarnings("unchecked")
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<String> annotationTypes = getSupportedAnnotationTypes();
        for (String annotationType : annotationTypes) {
            try {
                Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationType);
                Set<? extends Element> entityAnnotated = roundEnv.getElementsAnnotatedWith(annotationClass);
                for (TypeElement typeElement : ElementFilter.typesIn(entityAnnotated)) {
                    BuilderUtil builderUtil = new BuilderUtil(processingEnv, annotationClass);
                    String sqlString = builderUtil.findSqlStringConstant(typeElement);
                    if (sqlString != null)
                        if (builderUtil.checkForPojoParamsAnnotations(typeElement)) {
                            try {
                                if (typeElement.getAnnotation(NoParameters.class) != null) {
                                    // No params
                                    if (builderUtil.checkForParamsInSelect(typeElement,
                                            sqlString))
                                        if (annotationClass.equals(CCSelectExists.class))
                                            builderUtil.writeBuilder(typeElement,
                                                    null,
                                                    addExistsOverhead(sqlString));
                                        else
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
                                            if (annotationClass.equals(CCSelectExists.class))
                                                builderUtil.writeBuilder(typeElement,
                                                        parameters,
                                                        addExistsOverhead(sqlString));
                                            else
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
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    private String addExistsOverhead(String sqlString) {
        return "select exists ("
                + sqlString
                + ")";
    }
}
