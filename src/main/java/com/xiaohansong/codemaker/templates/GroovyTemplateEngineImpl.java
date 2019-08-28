package com.xiaohansong.codemaker.templates;

import com.google.common.collect.Maps;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.TemplateLanguage;
import groovy.text.GStringTemplateEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class GroovyTemplateEngineImpl extends BaseTemplateEngine {

    private GStringTemplateEngine groovyTemplateEngine = new GStringTemplateEngine();

    @Override
    protected TemplateLanguage supportedLanguage() {
        return TemplateLanguage.groovy;
    }

    @Override
    protected String doEvaluate(CodeTemplate template, Environment environment) {
        return render(template.getCodeTemplate(), adjustBindings(environment.bindings));
    }

    private String render(String templateContent, Map<String, Object> environment) {
        try {
            final StringWriter writer = new StringWriter();
            groovyTemplateEngine.createTemplate(templateContent).make(environment).writeTo(writer);
            return writer.toString();
        } catch (Exception e) {
            final RuntimeException reported = new RuntimeException(e);
            reported.setStackTrace(e.getStackTrace());
            throw reported;
        }
    }

    private Map<String, Object> adjustBindings(Map<String, Object> env) {
        final HashMap<String, Object> map = Maps.newHashMap(env);
        map.put("clazz", env.get("class"));
        return map;
    }

    @Override
    protected String generateClassName(String classNameTemplate, Map<String, Object> environment) {
        return render(classNameTemplate, adjustBindings(environment));
    }
}
