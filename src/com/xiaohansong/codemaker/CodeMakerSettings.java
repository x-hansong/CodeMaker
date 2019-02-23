package com.xiaohansong.codemaker;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerSettings.java, v 0.1 2017-01-28 9:30 hansong.xhs Exp $$
 */
@State(name = "CodeMakerSettings", storages = {@Storage(id = "app-default", file = "$APP_CONFIG$/CodeMaker-settings.xml")})
public class CodeMakerSettings implements PersistentStateComponent<CodeMakerSettings> {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = Logger.getInstance(CodeMakerSettings.class);

    public CodeMakerSettings() {
    }

    private void loadDefaultSettings() {
        try {
            Map<String, CodeTemplate> codeTemplates = new HashMap<>();
            codeTemplates.put("Model",
                    createCodeTemplate("Model.vm",
                            "#set($end = ${class0.className.length()} - 2)${class0.className.substring(0,${end})}", 1, CodeTemplate.DEFAULT_ENCODING));
            codeTemplates.put("Converter",
                    createCodeTemplate("Converter.vm", "${class0.className}Converter", 2, CodeTemplate.DEFAULT_ENCODING));
            codeTemplates.put("Specs2 Matcher",
                    createCodeTemplate("specs2-matcher.vm", "${class0.className}Matchers", 1, CodeTemplate.DEFAULT_ENCODING));
            codeTemplates.put("FieldComment",
                    createCodeTemplate("FieldComment.vm", "${class0.className}", 1, CodeTemplate.DEFAULT_ENCODING));

            this.codeTemplates = codeTemplates;
        } catch (Exception e) {
            LOGGER.error("loadDefaultSettings failed", e);
        }
    }

    @NotNull
    private CodeTemplate createCodeTemplate(String sourceTemplateName, String classNameVm, int classNumber, String fileEncoding) throws IOException {
        String velocityTemplate = FileUtil.loadTextAndClose(CodeMakerSettings.class.getResourceAsStream("/template/" + sourceTemplateName));
        return new CodeTemplate(sourceTemplateName,
                classNameVm, velocityTemplate, classNumber, fileEncoding);
    }

    /**
     * Getter method for property <tt>codeTemplates</tt>.
     *
     * @return property value of codeTemplates
     */
    public Map<String, CodeTemplate> getCodeTemplates() {
        if (codeTemplates == null) {
            loadDefaultSettings();
        }
        return codeTemplates;
    }

    @Setter
    private Map<String, CodeTemplate> codeTemplates;

    @Nullable
    @Override
    public CodeMakerSettings getState() {
        if (this.codeTemplates == null) {
            loadDefaultSettings();
        }
        return this;
    }

    @Override
    public void loadState(CodeMakerSettings codeMakerSettings) {
        XmlSerializerUtil.copyBean(codeMakerSettings, this);
    }

    public CodeTemplate getCodeTemplate(String template) {
        return codeTemplates.get(template);
    }

    public void removeCodeTemplate(String template) {
        codeTemplates.remove(template);
    }

}
