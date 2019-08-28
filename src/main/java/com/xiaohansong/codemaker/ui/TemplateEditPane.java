package com.xiaohansong.codemaker.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.ExceptionUtil;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.TemplateLanguage;
import com.xiaohansong.codemaker.templates.GeneratedSource;
import com.xiaohansong.codemaker.templates.PolyglotTemplateEngine;
import com.xiaohansong.codemaker.templates.TemplateEngine;
import com.xiaohansong.codemaker.templates.input.TestInput;
import com.xiaohansong.codemaker.templates.input.TestInputs;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hansong.xhs
 * @version $Id: TemplateEditPane.java, v 0.1 2017-01-31 9:06 hansong.xhs Exp $$
 */
public class TemplateEditPane {

    private JPanel     templateEdit;
    private JTextField templateNameText;
    private JSpinner   classNumberSpinner;
    private JTextField classNameText;
    private JTextField fileEncodingText;
    private JComboBox  templateLanguage;
    private JButton    testButton;
    private JComboBox<TestInput>  testInputs;
    private JSplitPane editorSplitPane;
    private JButton showTestInput;
    private JComboBox<TargetLanguage> targetLanguage;
    private TemplateEngine templateEngine = new PolyglotTemplateEngine();
    private TemplateEditAndTest templateEditAndTest;

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
           templateEditAndTest.refreshTemplateEditor();
        });
        classNumberSpinner.setValue(codeTemplate.getClassNumber());
        classNameText.setText(codeTemplate.getClassNameVm());
        fileEncodingText.setText(StringUtil.notNullize(codeTemplate.getFileEncoding(), CodeTemplate.DEFAULT_ENCODING));

        testButton.setText(null);
        testButton.setIcon(AllIcons.Actions.Execute);
        testButton.addActionListener(e -> testTemplate());

        final ListCellRenderer renderer = testInputs.getRenderer();
        TestInputs.getInputs().forEach(testInputs::addItem);
        //noinspection unchecked
        testInputs.setRenderer((l, item, index, selected, focused ) ->
                renderer.getListCellRendererComponent(l, item.getName(), index, selected, focused));

        testInputs.addItemListener(e -> templateEditAndTest.refreshTestInput());

        classNumberSpinner.setModel(new SpinnerNumberModel(codeTemplate.getClassNumber(), 1, 3, 1));
        classNumberSpinner.addChangeListener(e -> templateEditAndTest.refreshTestInput());

        templateEditAndTest = new TemplateEditAndTest(
                editorSplitPane,
                this::getSelectedTestInput,
                this::getClassNumber,
                codeTemplate.getCodeTemplate(),
                this::getTemplateLanguage,
                150);

        setUpShowTestInputButton();

        TargetLanguageSelect.initCombo(targetLanguage, codeTemplate.getTargetLanguage());
    }

    protected void setUpShowTestInputButton() {
        showTestInput.setIcon(AllIcons.General.ArrowDown);
        showTestInput.setToolTipText("Show Test Input");
        showTestInput.addActionListener(e -> {
            if(!templateEditAndTest.getTestInputShown()) {
                templateEditAndTest.toggleTestInputPane();
                showTestInput.setIcon(AllIcons.General.ArrowUp);
                editorSplitPane.setEnabled(true);
                showTestInput.setToolTipText("Hide Test Input");
            } else {
                templateEditAndTest.toggleTestInputPane();
                showTestInput.setIcon(AllIcons.General.ArrowDown);
                showTestInput.setToolTipText("Show Test Input");
                editorSplitPane.setEnabled(false);
            }
        });
    }

    private void testTemplate() {
        CodeTemplate template = new CodeTemplate(getTemplateName(), getClassName(), getTemplate(),
                getClassNumber(), getFileEncoding(), getTemplateLanguage(), getTargetLanguage());

        final List<ClassEntry> classEntries = inputClassIndices()
                .map(i ->
                     getSelectedTestInput().createInput("SomeClass" + i)
                )
                .collect(Collectors.toList());

        Object result;
        try {
            result = templateEngine.evaluate(template, classEntries, classEntries.get(0));
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
                  Editors.createSourceEditor(null, "txt",
                          error.getMessage() + "\n\n" + ExceptionUtil.getThrowableText(error), true)
                          .getComponent());
        }
        builder.show();
    }

    @NotNull
    private Stream<Integer> inputClassIndices() {
        return Stream.iterate(1, i -> i + 1)
                .limit(getClassNumber());
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
        return templateEditAndTest.getTemplateText();
    }

    public String getFileEncoding() {
        return fileEncodingText.getText();
    }

    public int getClassNumber() {
        return ((Number)classNumberSpinner.getValue()).intValue();
    }

    public String getTargetLanguage() {
        final TargetLanguage selectedItem = (TargetLanguage)targetLanguage.getSelectedItem();
        if(selectedItem == null) return "java";
        else return selectedItem.getFileType();
    }
}
