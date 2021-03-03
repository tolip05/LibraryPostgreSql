package com.cc.annotation.processor;

import com.cc.annotation.CCPagingSelect;
import com.cc.annotation.NoParameters;
import com.cc.annotation.Parameters;
import com.cc.annotation.Parameters.ParamTypes;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.cc.annotation.CCPagingSelect")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CCPagingSelectProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        Set<? extends Element> entityAnnotated = roundEnv.getElementsAnnotatedWith(CCPagingSelect.class);
        for (TypeElement typeElement : ElementFilter.typesIn(entityAnnotated)) {
            BuilderUtil builderUtil = new BuilderUtil(processingEnv, CCPagingSelect.class);
            CCPagingSelect ccPagingSelectAnnotation = typeElement.getAnnotation(CCPagingSelect.class);
            String sqlString = builderUtil.findSqlStringConstant(typeElement);
            if (sqlString != null)
                if (builderUtil.checkForPojoParamsAnnotations(typeElement)) {
                    try {
                        if (typeElement.getAnnotation(NoParameters.class) != null) {
                            // No params
                            Parameters parameters = new Parameters() {

                                @Override
                                public Class<? extends Annotation> annotationType() {
                                    return null;
                                }

                                @Override
                                public ParamTypes[] types() {
                                    return new ParamTypes[] { ParamTypes.Integer,
                                            ParamTypes.Integer };
                                }

                                @Override
                                public String[] names() {
                                    return new String[] { "rowBegin",
                                            "rowEnd" };
                                }
                            };
                            if (builderUtil.checkForParamsInSelect(typeElement,
                                    sqlString)) {
                                builderUtil.writeBuilder(typeElement,
                                        parameters,
                                        addPagingOverhead(sqlString,
                                                ccPagingSelectAnnotation.countRows()));
                            }
                        }
                        if (typeElement.getAnnotation(Parameters.class) != null) {
                            // With params
                            final Parameters origParameters = typeElement.getAnnotation(Parameters.class);
                            final List<String> paramNames = new ArrayList<String>();
                            paramNames.addAll(Arrays.asList(origParameters.names()));
                            paramNames.add("rowBegin");
                            paramNames.add("rowEnd");
                            final List<ParamTypes> paramTypes = new ArrayList<ParamTypes>();
                            paramTypes.addAll(Arrays.asList(origParameters.types()));
                            paramTypes.add(ParamTypes.Integer);
                            paramTypes.add(ParamTypes.Integer);
                            Parameters parameters = new Parameters() {

                                @Override
                                public Class<? extends Annotation> annotationType() {
                                    return origParameters.annotationType();
                                }

                                @Override
                                public ParamTypes[] types() {
                                    return paramTypes.toArray(new ParamTypes[0]);
                                }

                                @Override
                                public String[] names() {
                                    return paramNames.toArray(new String[0]);
                                }
                            };
                            if (builderUtil.checkParametersAnnotation(typeElement,
                                    parameters))
                                if (builderUtil.checkForSelectParametersCount(typeElement,
                                        origParameters,
                                        sqlString))
                                    builderUtil.writeBuilder(typeElement,
                                            parameters,
                                            addPagingOverhead(sqlString,
                                                    ccPagingSelectAnnotation.countRows()));
                        }
                    } catch (IOException e) {
                        LOG.getLog(getClass())
                                .error(e);
                    }
                }
        }
        return false;
    }

    private String addPagingOverhead(String sqlString,
                                     boolean countRows) {
        if (countRows)
            return "select * from (select COUNT(*) OVER(ORDER BY 0) rows_count, a.*, ROW_NUMBER() OVER (ORDER BY 0) rnum from ( "
                    + sqlString
                    + " ) a ) b where rnum  >= coalesce(?,rnum) and rnum <= coalesce(?,rnum) ";
        return "select * from (select a.*, ROW_NUMBER() OVER (ORDER BY 0) rnum from ( "
                + sqlString
                + " ) a ) b where rnum  >= coalesce(?,rnum) and rnum <= coalesce(?,rnum) ";
    }
}
