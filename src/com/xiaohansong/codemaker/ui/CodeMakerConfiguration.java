package com.xiaohansong.codemaker.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBTabbedPane;
import com.xiaohansong.codemaker.CodeMakerSettings;
import com.xiaohansong.codemaker.CodeTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaohansong.codemaker.ui.UiTools.onMouseClick;
import static com.xiaohansong.codemaker.ui.UiTools.with;

/**
 * @author hansong.xhs
 * @version $Id: CodeMakerConfiguration.java, v 0.1 2017-02-01 11:32 hansong.xhs Exp $$
 */
public class CodeMakerConfiguration {
    private JPanel                        mainPane;
    private JBTabbedPane                  tabbedPane;
    private Map<String, TemplateEditPane> editPaneMap = new ConcurrentHashMap<>();
    private Map<Integer, String>          keyByIndex  = new ConcurrentHashMap<>();
    private Map<String, CodeTemplate>     codeTemplates;

    public CodeMakerConfiguration(CodeMakerSettings settings) {
        this.codeTemplates = new LinkedHashMap<>(settings.getCodeTemplates());
        codeTemplates.forEach(this::addNewTab);
        addPlusTab();
        selectTab(0);
    }

    protected void createNewTemplate() {
        final String key = UUID.randomUUID().toString();
        final CodeTemplate template = CodeTemplate.empty("Untitled");
        final int index = addNewTab(key, template);
        codeTemplates.put(key, template);
        // select the last template tab - that is not the plus button
        selectTab(index);
    }

    private void selectTab(int index) {
        if(index >= tabbedPane.getTabCount() - 1) {
            index = tabbedPane.getTabCount() - 2;
        }
        if(index < 0) {
            index = 0;
        }
        tabbedPane.setSelectedIndex(index);
    }

    public void refresh(Map<String, CodeTemplate> templates) {
        tabbedPane.removeAll();
        editPaneMap.clear();
        templates.forEach(this::addNewTab);
        addPlusTab();
    }

    private void addPlusTab() {
        tabbedPane.addTab("+", new JPanel());
        final JLabel addButton = new JLabel(AllIcons.Welcome.CreateNewProject);
        addButton.setToolTipText("Add New Template");
        addButton.addMouseListener(onMouseClick(e -> createNewTemplate()));
        final int index = tabbedPane.getTabCount() - 1;
        tabbedPane.setTabComponentAt(index, addButton);
        tabbedPane.setEnabledAt(index, false);
    }

    private void tabTitleChanged(int index, String updated) {
        tabbedPane.setTabComponentAt(index, tabTitleComponent(updated, keyByIndex.get(index)));
    }

    private int addNewTab(String key, CodeTemplate codeTemplate) {
        final int index = tabbedPane.getTabCount() ;
        TemplateEditPane editPane = new TemplateEditPane(codeTemplate,
                updated -> tabTitleChanged(index, updated));
        tabbedPane.insertTab(key, null,  editPane.getTemplateEdit(), null, index);
        tabbedPane.setTabComponentAt(index, tabTitleComponent(codeTemplate.getName(), key));
        editPaneMap.put(key, editPane);
        keyByIndex.put(index, key);
        return index;
    }

    private Component tabTitleComponent(final String title, final String templateKey) {
        return with(
            new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)), p -> {
                    p.add(with(new JLabel(title), t -> t.setOpaque(false)));
                    p.setOpaque(false);
                    p.add(with(new JLabel(AllIcons.Actions.Close), b  -> {
                        b.setOpaque(false);
                        b.setToolTipText("Delete Template");
                        b.addMouseListener(onMouseClick(e -> onDeleteClicked(title, templateKey)));
                    }));
        });
    }

    private void onDeleteClicked(String title, String templateKey) {
        int result = Messages.showYesNoDialog("Delete this template '" + title + "' ?", "Delete", null);
        if (result == Messages.OK) {
            codeTemplates.remove(templateKey);
            refresh(codeTemplates);
        }
    }

    public Map<String, CodeTemplate> getTabTemplates() {
        Map<String, CodeTemplate> map = new LinkedHashMap<>();
        editPaneMap.forEach((key, value) -> {
            CodeTemplate codeTemplate = new CodeTemplate(value.getTemplateName(), value
                    .getClassName(), value.getTemplate(), value.getClassNumber(), value.getFileEncoding(), value.getTemplateLanguage(), value.getTargetLanguage());
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
