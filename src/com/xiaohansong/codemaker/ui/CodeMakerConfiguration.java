package com.xiaohansong.codemaker.ui;

import com.intellij.ui.components.JBTabbedPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.CodeTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerConfiguration.java, v 0.1 2017-02-01 11:32 hansong.xhs Exp $$
 */
public class CodeMakerConfiguration {
    private JPanel                        mainPane;
    private JButton                       addTemplateButton;
    private JBTabbedPane                  tabbedPane;
    private Map<String, TemplateEditPane> editPaneMap;

    public CodeMakerConfiguration(CodeMakerSettings settings) {
        tabbedPane = new JBTabbedPane();
        editPaneMap = new HashMap<>();
        addTemplateButton.addActionListener(e -> {
            TemplateEditPane editPane = new TemplateEditPane(settings, "", this);
            String title = "Untitled";
            tabbedPane.addTab(title, editPane.getTemplateEdit());
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            editPaneMap.put(title, editPane);
        });
        resetTabPane(settings);
        GridConstraints constraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, 300), null, 0, true);
        mainPane.add(tabbedPane, constraints);
    }

    public void refresh(CodeMakerSettings settings) {
        tabbedPane.removeAll();
        editPaneMap.clear();
        resetTabPane(settings);
    }

    private void resetTabPane(CodeMakerSettings settings) {
        settings.getCodeTemplates().forEach((key, value) -> {
            TemplateEditPane editPane = new TemplateEditPane(settings, key, this);
            tabbedPane.addTab(key, editPane.getTemplateEdit());
            editPaneMap.put(key, editPane);
        });
    }

    public Map<String, CodeTemplate> getTabTemplates() {
        Map<String, CodeTemplate> map = new HashMap<>();
        editPaneMap.forEach((key, value) -> {
            CodeTemplate codeTemplate = new CodeTemplate(value.getTemplateName(), value
                .getClassName(), value.getTemplate(), value.getClassNumber());
            map.put(codeTemplate.getName(), codeTemplate);
        });
        return map;
    }

    /**
     * Getter method for property <tt>mainPane</tt>.
     *
     * @return property value of mainPane
     */
    public JPanel getMainPane() {
        return mainPane;
    }
}
