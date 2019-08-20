package com.xiaohansong.codemaker.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.TemplateLanguage;
import com.xiaohansong.codemaker.templates.GeneratedSource;
import com.xiaohansong.codemaker.templates.PolyglotTemplateEngine;
import com.xiaohansong.codemaker.templates.TemplateEngine;
import com.xiaohansong.codemaker.templates.input.TestInput;
import com.xiaohansong.codemaker.templates.input.TestInputs;
import com.xiaohansong.codemaker.util.CodeMakerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author hansong.xhs
 * @version $Id: TemplateEditPane.java, v 0.1 2017-01-31 9:06 hansong.xhs Exp $$
 */
public class TemplateEditPane {

    private JPanel     templateEdit;
    private JTextField templateNameText;
    private JTextField classNumberText;
    private JTextField classNameText;
    private JTextField fileEncodingText;
    private JComboBox  templateLanguage;
    private JButton    testButton;
    private JPanel editorHolder;
    private JComboBox<TestInput>  testInputs;
    private JSplitPane editorSplitPane;
    private JPanel testInputHolder;
    private JButton showTestInput;
    private JComboBox<String> targetLanguage;
    private Editor     editor;
    private TemplateEngine templateEngine = new PolyglotTemplateEngine();

    public TemplateEditPane(CodeTemplate codeTemplate, Consumer<String> titleChanged) {
        if (codeTemplate == null) {
            throw new NullPointerException("codeTemplate is null!");
        }

        templateNameText.setText(codeTemplate.getName());
        templateNameText.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                titleChanged.accept(templateNameText.getText());
            }
        });

        templateLanguage.setSelectedItem(codeTemplate.getTemplateLanguage().name());
        templateLanguage.addActionListener(e -> {
           initTemplateEditor(getTemplate(), getTemplateLanguage());
        });
        classNumberText.setText(String.valueOf(codeTemplate.getClassNumber()));
        classNameText.setText(codeTemplate.getClassNameVm());
        fileEncodingText.setText(StringUtil.notNullize(codeTemplate.getFileEncoding(), CodeTemplate.DEFAULT_ENCODING));
        initTemplateEditor(codeTemplate.getCodeTemplate(), codeTemplate.getTemplateLanguage());

        testButton.setText(null);
        testButton.setIcon(AllIcons.Actions.Execute);

        final ListCellRenderer renderer = testInputs.getRenderer();
        TestInputs.getInputs().forEach(testInputs::addItem);
        //noinspection unchecked
        testInputs.setRenderer((l, item, index, selected, focused ) ->
                renderer.getListCellRendererComponent(l, item.getName(), index, selected, focused));

        testInputs.addItemListener(e -> showSelectedTestInput());

        showSelectedTestInput();

        setUpShowTestInputButton();

        setTargetLanguagesList(codeTemplate.getTargetLanguage());
    }

    protected void setUpShowTestInputButton() {
        editorSplitPane.setDividerLocation(0);
        showTestInput.setIcon(AllIcons.General.ArrowDown);
        showTestInput.setToolTipText("Show Test Input");
        showTestInput.addActionListener(e -> {
            if(showTestInput.getIcon() == AllIcons.General.ArrowDown) {
                editorSplitPane.setDividerLocation(120);
                showTestInput.setIcon(AllIcons.General.ArrowUp);
                editorSplitPane.setEnabled(true);
                showTestInput.setToolTipText("Hide Test Input");
            } else {
                editorSplitPane.setDividerLocation(0);
                showTestInput.setIcon(AllIcons.General.ArrowDown);
                showTestInput.setToolTipText("Show Test Input");
                editorSplitPane.setEnabled(false);
            }
        });
    }

    protected void setTargetLanguagesList(String selected) {
        targetLanguage.addItem("java");
        targetLanguage.addItem("scala");
        Arrays.stream(FileTypeManager.getInstance().getRegisteredFileTypes())
                .filter(tp -> !tp.isBinary() && !tp.getDefaultExtension().isEmpty() &&
                        !tp.getDefaultExtension().equalsIgnoreCase("java") &&
                        !tp.getDefaultExtension().equalsIgnoreCase("scala")
                )
                .forEach(tp -> targetLanguage.addItem(tp.getDefaultExtension()));
        targetLanguage.setSelectedItem(selected);
    }


    protected void showSelectedTestInput() {
        if(testInputHolder.getComponentCount() > 0) {
            testInputHolder.remove(0);
        }
        final TestInput selectedTestInput = getSelectedTestInput();
        testInputHolder.add(Editors.createSourceEditor(null, selectedTestInput.getLanguage(), selectedTestInput.getSource("SomeClass"), true).getComponent());
    }

    private void initTemplateEditor(String template, TemplateLanguage lang) {
        if(editor !=null) {
            editorHolder.remove(editor.getComponent());
        }
        final EditorFactory factory = EditorFactory.getInstance();
        final Document content = factory.createDocument(template);
        final FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(lang.fileType);
        editor = factory.createEditor(content, null, fileType, false);
        editor.getSettings().setRefrainFromScrolling(false);
        editorHolder.add(editor.getComponent(), BorderLayout.CENTER);
        testButton.addActionListener(e -> testTemplate());
    }

    private void testTemplate() {
        CodeTemplate template = new CodeTemplate(getTemplateName(), getClassName(), getTemplate(),
                getClassNumber(), getFileEncoding(), getTemplateLanguage(), getTargetLanguage());
        ClassEntry classEntry = getSelectedTestInput().createInput("SomeClass");

        Object result;
        try {
            result = templateEngine.evaluate(template, newArrayList(classEntry), classEntry);
        } catch (Throwable e) {
            result = e;
        }

        final DialogBuilder builder = new DialogBuilder(this.templateEdit);
        builder.addCloseButton().setText("Close");

        if(result instanceof GeneratedSource) {
            builder.setTitle(((GeneratedSource) result).getClassName());
            builder.setCenterPanel(
                    Editors.createSourceEditor(null, getTargetLanguage(), ((GeneratedSource) result).getContent(), true).getComponent());
        } else {
            Throwable error = (Throwable) result;
            builder.setTitle("Failed!");
            builder.setCenterPanel(
                  Editors.createSourceEditor(null, "txt", error.getMessage(), true).getComponent());
        }
        builder.show();
    }

    private TestInput getSelectedTestInput() {
        return testInputs.getItemAt(testInputs.getSelectedIndex());
    }

    /**
     * Getter method for property <tt>templateEdit</tt>.
     *
     * @return property value of templateEdit
     */
    public JPanel getTemplateEdit() {
        return templateEdit;
    }

    public String getClassName() {
        return classNameText.getText();
    }

    public String getTemplateName() {
        return templateNameText.getText();
    }

    public TemplateLanguage getTemplateLanguage() {
        return TemplateLanguage.valueOf(String.valueOf(templateLanguage.getSelectedItem()));
    }

    public String getTemplate() {
        return editor.getDocument().getText();
    }

    public String getFileEncoding() {
        return fileEncodingText.getText();
    }

    /**
     *
     * @return -1 if classNumberText is not number
     */
    public int getClassNumber() {
        if (CodeMakerUtil.isNumeric(classNumberText.getText())) {
            return Integer.parseInt(classNumberText.getText());
        }
        return -1;
    }

    public String getTargetLanguage() {
        return targetLanguage.getSelectedItem().toString();
    }
}
