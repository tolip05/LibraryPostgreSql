package com.cc.annotation.processor;

import com.cc.annotation.*;
import com.cc.annotation.Parameters.ParamTypes;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuilderUtil {
    private static final String BUILDER_SUB_PACKAGE = "builder";
    private static final String BUILDER_SUFFIX = "Builder";
    private PrintWriter pw;
    private Class<? extends Annotation> annotationClass;
    private boolean isBatchUpdate;
    private boolean isLargeObjectSelect;
    private ProcessingEnvironment processingEnv;

    public BuilderUtil(ProcessingEnvironment processingEnv,
                       Class<? extends Annotation> annotationClass) {
        this.processingEnv = processingEnv;
        this.annotationClass = annotationClass;
        this.isBatchUpdate = annotationClass.equals(CCBatchUpdate.class);
        this.isLargeObjectSelect = annotationClass.equals(CCSelectLargeObject.class);
    }

    public void writeBuilder(TypeElement typeElement,
                             Parameters parameters,
                             String sqlString)
            throws IOException {
        String elementPackage = processingEnv.getElementUtils()
                .getPackageOf(typeElement)
                .getQualifiedName()
                .toString();
        JavaFileObject sourceFile = processingEnv.getFiler()
                .createSourceFile(elementPackage
                                + "."
                                + BUILDER_SUB_PACKAGE
                                + "."
                                + typeElement.getSimpleName()
                                .toString()
                                + BUILDER_SUFFIX,
                        typeElement);
        pw = new PrintWriter(new OutputStreamWriter(sourceFile.openOutputStream(),
                StandardCharsets.UTF_8),
                true);
        addClassHeader(typeElement,
                elementPackage,
                sqlString);
        addCreateMethod(parameters != null);
        addBuildInterface();
        if (parameters != null)
            for (int i = 1; i <= parameters.types().length; i++) {
                addInterfaceHeader(i,
                        i == parameters.types().length);
                ParamTypes pt = parameters.types()[i
                        - 1];
                addInterfaceBody(pt,
                        nullGetParamName(parameters,
                                i),
                        nullGetOriginalParamName(parameters,
                                i),
                        i,
                        i == parameters.types().length);
                pw.println("}");
            }
        addSteps(parameters);
        pw.println("}");
        pw.flush();
        pw.close();
    }

    public void writeBuilderForLargeObject(TypeElement typeElement,
                                           Parameters parameters,
                                           String sqlString)
            throws IOException {
        String elementPackage = processingEnv.getElementUtils()
                .getPackageOf(typeElement)
                .getQualifiedName()
                .toString();
        JavaFileObject sourceFile = processingEnv.getFiler()
                .createSourceFile(elementPackage
                                + "."
                                + BUILDER_SUB_PACKAGE
                                + "."
                                + typeElement.getSimpleName()
                                .toString()
                                + BUILDER_SUFFIX,
                        typeElement);
        pw = new PrintWriter(new OutputStreamWriter(sourceFile.openOutputStream(),
                StandardCharsets.UTF_8),
                true);
        addClassHeader(typeElement,
                elementPackage,
                sqlString);
        addCreateMethod(parameters != null);
        if (isLargeObjectSelect)
            addConsummerInterface();
        addBuildInterface();
        if (parameters != null)
            for (int i = 1; i <= parameters.types().length; i++) {
                addInterfaceHeader(i,
                        i == parameters.types().length);
                ParamTypes pt = parameters.types()[i
                        - 1];
                addInterfaceBody(pt,
                        nullGetParamName(parameters,
                                i),
                        nullGetOriginalParamName(parameters,
                                i),
                        i,
                        i == parameters.types().length);
                pw.println("}");
            }
        addSteps(parameters);
        pw.println("}");
        pw.flush();
        pw.close();

    }

    private void addConsummerInterface() {
        pw.println();
        pw.println("public static interface ConsummerStep {");
        pw.println("	BuildStep defineConsummer(java.util.function.BiConsumer<byte[], Integer> consummer);");
        pw.println("}");

    }

    private void addClassHeader(TypeElement typeElement,
                                String elementPackage,
                                String sqlString) {
        pw.println("package "
                + elementPackage
                + "."
                + BUILDER_SUB_PACKAGE
                + ";");
        pw.println();
        pw.println("public class "
                + typeElement.getSimpleName()
                .toString()
                + BUILDER_SUFFIX
                + "{");
        pw.println();
        pw.println("private static String sqlString = \""
                + sqlString.replaceAll("\n",
                "")
                + "\";");
    }

    private void addCreateMethod(boolean hasParameters) {
        pw.println();
        if (hasParameters)
            pw.println("public static Param1 create() {");
        else
            pw.println("public static BuildStep create() {");
        pw.println("	return new Steps();");
        pw.println("}");
    }

    private void addBuildInterface() {
        pw.println();
        if (isBatchUpdate)
            pw.println("public static interface BuildStep extends Param1{");
        else
            pw.println("public static interface BuildStep {");
        if (annotationClass.equals(CCUpdate.class))
            pw.println("	com.cc.pojo.PojoUpdate build();");
        else if (annotationClass.equals(CCBatchUpdate.class))
            pw.println("	com.cc.pojo.PojoBatchUpdate build();");
        else if (annotationClass.equals(CCSelectLargeObject.class))
            pw.println("	com.cc.pojo.LargeObjectSelect build();");
        else
            pw.println("	com.cc.pojo.PojoSelect build();");
        pw.println("}");
    }

    private void addInterfaceHeader(int paramIndex,
                                    boolean last) {
        pw.println();
        pw.println("public static interface Param"
                + paramIndex
                + " {");
    }

    private void addInterfaceBody(ParamTypes paramType,
                                  String paramName,
                                  String origParamName,
                                  int paramIndex,
                                  boolean last) {
        String methodReturn = last ? isLargeObjectSelect ? "		ConsummerStep" : "		BuildStep"
                : "		Param"
                + (paramIndex
                + 1);
        pw.println(methodReturn
                + " set"
                + paramName
                + "("
                + paramType.className
                + (paramType.subClassName != null ? "<"
                + paramType.subClassName
                + ">"
                : "")
                + " "
                + origParamName
                + ");");
        pw.println();
    }

    private String nullGetParamName(Parameters parameters,
                                    int paramIndex) {
        if (parameters.names().length >= paramIndex)
            return "Param"
                    + paramIndex
                    + "_"
                    + capitalizeFirstLetter(parameters.names()[paramIndex
                    - 1]);
        else
            return "Param"
                    + paramIndex;
    }

    private String nullGetOriginalParamName(Parameters parameters,
                                            int paramIndex) {
        return parameters.names()[paramIndex
                - 1];
    }

    private String capitalizeFirstLetter(String paramName) {
        return paramName.substring(0,
                1)
                .toUpperCase()
                + paramName.substring(1);
    }

    private void addSteps(Parameters parameters) {
        pw.println();
        if (parameters == null) {
            // No params
            pw.print("private static class Steps implements BuildStep {");
        } else {
            // Has params
            pw.print("private static class Steps implements ");
            for (int i = 1; i <= parameters.types().length; i++) {
                pw.print("Param"
                        + i
                        + ", ");
            }
            if (isLargeObjectSelect)
                pw.print("ConsummerStep, ");
            pw.println("BuildStep {");
            pw.println();
            if (isBatchUpdate) {
                pw.println("private java.util.List<java.util.List<com.cc.pojo.Param>> allParams = new java.util.ArrayList<java.util.List<com.cc.pojo.Param>>();");
                pw.println();
            }
            if (isLargeObjectSelect) {
                pw.println("private java.util.function.BiConsumer<byte[], Integer> consummer;");
                pw.println();
            }
            pw.println("private java.util.List<com.cc.pojo.Param> params = new java.util.ArrayList<com.cc.pojo.Param>();");
            pw.println();
            for (int i = 1; i <= parameters.types().length; i++) {
                ParamTypes pt = parameters.types()[i
                        - 1];
                addInterfaceMethodImplementation(pt,
                        nullGetParamName(parameters,
                                i),
                        nullGetOriginalParamName(parameters,
                                i),
                        i,
                        i == parameters.types().length);
            }
        }
        if (isLargeObjectSelect)
            addDefineConsummerMethod();
        addBuildMethod(parameters != null);
        pw.println();
        pw.println("}");

    }

    private void addInterfaceMethodImplementation(ParamTypes paramType,
                                                  String paramName,
                                                  String origParamName,
                                                  int paramIndex,
                                                  boolean last) {
        String methodReturn = last ? isLargeObjectSelect ? "ConsummerStep" : "BuildStep"
                : "Param"
                + (paramIndex
                + 1);
        pw.println("	public "
                + methodReturn
                + " set"
                + paramName
                + "("
                + paramType.className
                + (paramType.subClassName != null ? "<"
                + paramType.subClassName
                + ">"
                : "")
                + " "
                + origParamName
                + "){");
        if (isBatchUpdate
                && paramIndex == 1) {
            pw.println("		if(!params.isEmpty()){");
            pw.println("			allParams.add(params);");
            pw.println("			params = new java.util.ArrayList<com.cc.pojo.Param>();");
            pw.println("		}");
        }
        pw.println("		params.add(new com.cc.pojo.Param(com.cc.annotation.Parameters.ParamTypes."
                + paramType.toString()
                + ", "
                + origParamName
                + "));");
        pw.println("	return this;");
        pw.println("	}");
        pw.println();
    }

    private void addBuildMethod(boolean hasParams) {
        pw.println();
        String statementClassName = annotationClass.equals(CCUpdate.class) ? "PojoUpdate"
                : annotationClass.equals(CCBatchUpdate.class) ? "PojoBatchUpdate"
                : annotationClass.equals(CCSelectLargeObject.class) ? "LargeObjectSelect"
                : "PojoSelect";
        pw.println("	public com.cc.pojo."
                + statementClassName
                + " build() {");
        if (hasParams) {
            if (isBatchUpdate) {
                pw.println("		allParams.add(params);");
                pw.println("		return new com.cc.pojo."
                        + statementClassName
                        + "(this.getClass().getName(), sqlString, allParams);");
            } else if (isLargeObjectSelect)
                pw.println("		return new com.cc.pojo."
                        + statementClassName
                        + "(this.getClass().getName(), sqlString, consummer, params);");
            else
                pw.println("		return new com.cc.pojo."
                        + statementClassName
                        + "(this.getClass().getName(), sqlString, params);");
        } else
            pw.println("		return new com.cc.pojo."
                    + statementClassName
                    + "(this.getClass().getName(), sqlString);");
        pw.println("	}");
    }

    private void addDefineConsummerMethod() {
        pw.println("	public BuildStep defineConsummer(java.util.function.BiConsumer<byte[], Integer> consummer){");
        pw.println("		this.consummer = consummer;");
        pw.println("		return this;");
        pw.println("	}");
    }

    public boolean checkParametersAnnotation(TypeElement typeElement,
                                             Parameters parameters) {
        if (parameters.names().length == 0
                && parameters.types().length == 0) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Parameters annotation must have one or more parameters' names and types declared!",
                            typeElement);
            return false;
        }
        Pattern p = Pattern.compile("[^a-zA-Z_$0-9]",
                Pattern.CASE_INSENSITIVE);
        for (String parameterName : parameters.names())
            if (p.matcher(parameterName)
                    .find()) {
                processingEnv.getMessager()
                        .printMessage(Kind.ERROR,
                                "Declared parameter's name '"
                                        + parameterName
                                        + "' contains special character! Use java conventions for names!",
                                typeElement);
                return false;
            }
        if (parameters.names().length > parameters.types().length) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Declared parameters' names count is greater than declared parameters' types. They must be equal!",
                            typeElement);
            return false;
        }
        if (parameters.types().length > parameters.names().length) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Declared parameters' types count is greater than declared parameters' names. They must be equal!",
                            typeElement);
            return false;
        }
        return true;
    }

    public boolean checkForSelectParametersCount(TypeElement typeElement,
                                                 Parameters parameters,
                                                 String sqlString) {
        int declaredParametersCount = parameters.types().length;
        int parametersCountInSelect = findParametersCountInSelect(sqlString);
        if (declaredParametersCount > parametersCountInSelect) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Count of annotated parameters is greater than that in sqlString!",
                            typeElement);
            return false;
        }
        if (declaredParametersCount < parametersCountInSelect) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Count of annotated parameters is less than that in sqlString!",
                            typeElement);
            return false;
        }
        return true;
    }

    public String findSqlStringConstant(TypeElement typeElement) {
        for (VariableElement e : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
            if (e.getModifiers()
                    .containsAll(Arrays.asList(new Modifier[] { Modifier.PUBLIC,
                            Modifier.STATIC,
                            Modifier.FINAL }))
                    && e.getSimpleName()
                    .toString()
                    .equals("sqlString"))
                return (String) e.getConstantValue();
        }
        processingEnv.getMessager()
                .printMessage(Kind.ERROR,
                        "Class must have 'public static final sqlString' property declared!",
                        typeElement);
        return null;
    }

    private int findParametersCountInSelect(String sqlString) {
        int count = 0;
        for (char c : sqlString.toCharArray())
            if (c == '?')
                count++;
        return count;
    }

    public boolean checkForParamsInSelect(TypeElement typeElement,
                                          String sqlString) {
        if (findParametersCountInSelect(sqlString) > 0) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Class is annotated with @NoParameters but there are params in sqlString!",
                            typeElement);
            return false;
        }
        return true;
    }

    public boolean checkForPojoParamsAnnotations(TypeElement typeElement) {
        if (typeElement.getAnnotation(NoParameters.class) == null
                && typeElement.getAnnotation(Parameters.class) == null) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Class annotated with @"
                                    + annotationClass.getSimpleName()
                                    + " must also be annotated with either @NoParameters or @Parameters!",
                            typeElement);
            return false;
        }
        return true;
    }

    public boolean checkForPojoParamsAnnotationsInLargeObjectSelect(TypeElement typeElement) {
        if (typeElement.getAnnotation(NoParameters.class) != null
                || typeElement.getAnnotation(Parameters.class) == null) {
            processingEnv.getMessager()
                    .printMessage(Kind.ERROR,
                            "Class annotated with @"
                                    + annotationClass.getSimpleName()
                                    + " must also be annotated with @Parameters!",
                            typeElement);
            return false;
        }
        return true;
    }

    public boolean checkForOneFieldInSelectStamenet(TypeElement typeElement,
                                                    String sqlString) {
        String fieldsPattern = "SELECT(.*)FROM";
        Matcher fieldsMatcher = Pattern.compile(fieldsPattern)
                .matcher(sqlString.toUpperCase());
        if (fieldsMatcher.find()) {
            String fields = fieldsMatcher.group(1);
            if (fields.contains(",")) {
                processingEnv.getMessager()
                        .printMessage(Kind.ERROR,
                                "Class annotated with @"
                                        + annotationClass.getSimpleName()
                                        + " must contains select with only OID column in the table!",
                                typeElement);
                return false;
            }
        }
        return true;
    }
}
