package com.cc.annotation.processor;

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

@SupportedAnnotationTypes({ "com.cc.annotation.CCSelectLargeObject" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CCSelectLargeObjectProcessor extends AbstractProcessor {
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
                    BuilderUtil builderUtil = new BuilderUtil(processingEnv,
                            annotationClass);
                    String sqlString = builderUtil.findSqlStringConstant(typeElement);
                    if (sqlString != null)
                        if (builderUtil.checkForPojoParamsAnnotationsInLargeObjectSelect(typeElement)
                                && builderUtil.checkForOneFieldInSelectStamenet(typeElement,
                                sqlString)) {
                            try {
                                if (typeElement.getAnnotation(Parameters.class) != null) {
                                    // With params
                                    Parameters parameters = typeElement.getAnnotation(Parameters.class);
                                    if (builderUtil.checkParametersAnnotation(typeElement,
                                            parameters))
                                        if (builderUtil.checkForSelectParametersCount(typeElement,
                                                parameters,
                                                sqlString))
                                            builderUtil.writeBuilderForLargeObject(typeElement,
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

}
