package com.xiaohansong.codemaker.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.CodeTemplate;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerConfigurable.java, v 0.1 2017-01-31 9:09 hansong.xhs Exp $$
 */
public class CodeMakerConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    private CodeMakerSettings      settings;

    private CodeMakerConfiguration configuration;

    public CodeMakerConfigurable() {
        settings = ServiceManager.getService(CodeMakerSettings.class);
    }

    @NotNull
    @Override
    public String getId() {
        return "plugins.codemaker";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "CodeMaker";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "help.codemaker.configuration";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (configuration == null) {
            configuration = new CodeMakerConfiguration(settings);
        }
        return configuration.getMainPane();
    }

    /**
     * Compare the data to see if we are modified
     *
     * @return true if the settings should be 'applied'
     */
    @Override
    public boolean isModified() {
        if (settings.getCodeTemplates().size() != configuration.getTabTemplates().size()) {
            return true;
        }
        for (Map.Entry<String, CodeTemplate> entry : configuration.getTabTemplates().entrySet()) {
            CodeTemplate codeTemplate = settings.getCodeTemplate(entry.getKey());
            if (codeTemplate == null || !codeTemplate.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        for (Map.Entry<String, CodeTemplate> entry : configuration.getTabTemplates().entrySet()) {
            if (!entry.getValue().isValid()) {
                throw new ConfigurationException(
                    "Not property can be empty and classNumber should be a number");
            }
        }
        settings.setCodeTemplates(configuration.getTabTemplates());
        configuration.refresh(settings.getCodeTemplates());
    }

    @Override
    public void reset() {
        if (configuration != null) {
            configuration.refresh(settings.getCodeTemplates());
        }
    }

    @Override
    public void disposeUIResources() {
        this.configuration = null;
    }
}
