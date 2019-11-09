package com.xiaohansong.codemaker.templates;

import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.TemplateLanguage;
import com.xiaohansong.codemaker.util.VelocityUtil;

import java.util.Map;

public class VelocityTemplateEngine extends BaseTemplateEngine {

    @Override
    protected TemplateLanguage supportedLanguage() {
        return TemplateLanguage.vm;
    }

    protected String doEvaluate(CodeTemplate template, Environment environment) {
        return VelocityUtil.evaluate(template.getCodeTemplate(), environment.bindings);
    }

    @Override
    protected String generateClassName(String classNameTemplate, Map<String, Object> environment) {
        return VelocityUtil.evaluate(classNameTemplate, environment);
    }

}
