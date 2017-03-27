package com.xiaohansong.codemaker;

import java.util.HashMap;
import java.util.Map;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

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
            String converterVm = FileUtil.loadTextAndClose(CodeMakerSettings.class.getResourceAsStream("/template/Converter.vm"));
            String modelVm = FileUtil.loadTextAndClose(CodeMakerSettings.class.getResourceAsStream("/template/Model.vm"));
            Map<String, CodeTemplate> codeTemplates = new HashMap<>();
            codeTemplates.put("Converter", new CodeTemplate("Converter",
                    "${class0.className}Converter", converterVm, 2));
            codeTemplates.put("Model", new CodeTemplate("Model",
                    "#set($end = ${class0.className.length()} - 2)${class0.className.substring(0,${end})}",
                    modelVm, 1));
            this.codeTemplates = codeTemplates;
        } catch (Exception e) {
            LOGGER.error("loadDefaultSettings failed", e);
        }
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
