package com.xiaohansong.codemaker.ui;

import com.google.common.collect.Streams;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBTabbedPane;
import com.xiaohansong.codemaker.ClassEntry;
import com.xiaohansong.codemaker.CodeTemplate;
import com.xiaohansong.codemaker.TemplateLanguage;
import com.xiaohansong.codemaker.templates.GeneratedSource;
import com.xiaohansong.codemaker.templates.PolyglotTemplateEngine;
import com.xiaohansong.codemaker.templates.TemplateEngine;
import com.xiaohansong.codemaker.templates.input.TestInput;
import com.xiaohansong.codemaker.templates.input.TestInputs;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
    private JPanel editorHolder;
    private JComboBox<TestInput>  testInputs;
    private JSplitPane editorSplitPane;
    private JPanel testInputHolder;
    private Boolean testInputShown = false;
    private JButton showTestInput;
    private JComboBox<TargetLanguage> targetLanguage;
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
        classNumberSpinner.setValue(codeTemplate.getClassNumber());
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

        testInputs.addItemListener(e -> refreshTestInputPane());

        classNumberSpinner.setModel(new SpinnerNumberModel(codeTemplate.getClassNumber(), 1, 3, 1));
        classNumberSpinner.addChangeListener(e -> refreshTestInputPane());

        refreshTestInputPane();

        setUpShowTestInputButton();

        setTargetLanguagesList(codeTemplate.getTargetLanguage());
    }

    protected void setUpShowTestInputButton() {
        editorSplitPane.setDividerLocation(0);
        // when split pane is resized (layout is called) it may move the divider
        editorSplitPane.addPropertyChangeListener("dividerLocation", e -> {
            if(!testInputShown && (Integer)e.getNewValue() > 0) {
                SwingUtilities.invokeLater(() -> editorSplitPane.setDividerLocation(0));
            }
        });
        showTestInput.setIcon(AllIcons.General.ArrowDown);
        showTestInput.setToolTipText("Show Test Input");
        showTestInput.addActionListener(e -> {
            if(!testInputShown) {
                testInputShown = true;
                editorSplitPane.setDividerLocation(120);
                showTestInput.setIcon(AllIcons.General.ArrowUp);
                editorSplitPane.setEnabled(true);
                showTestInput.setToolTipText("Hide Test Input");
            } else {
                testInputShown = false;
                editorSplitPane.setDividerLocation(0);
                showTestInput.setIcon(AllIcons.General.ArrowDown);
                showTestInput.setToolTipText("Show Test Input");
                editorSplitPane.setEnabled(false);
            }
        });
    }

    protected void setTargetLanguagesList(String selected) {

        // this sets the width of the combobox
        targetLanguage.setPrototypeDisplayValue(new TargetLanguage("JavaScript 1.1", ""));

        final TargetLanguage[] popular = {
                new TargetLanguage("Java", "java"),
                new TargetLanguage("Scala", "scala"),
                new TargetLanguage("Kotlin", "kt"),
                new TargetLanguage("JavaScript", "js"),
                new TargetLanguage("TypeScript", "ts"),
                new TargetLanguage("SQL", "sql")
        };

        final Set<String> popularExts = Stream.of(popular)
                .map(TargetLanguage::getFileType)
                .collect(Collectors.toSet());

        final Stream<TargetLanguage> otherRegistered = Language.getRegisteredLanguages().stream()
                .filter(l -> {
                    if (l.getAssociatedFileType() != null) {
                        final String ext = l.getAssociatedFileType().getDefaultExtension();
                        return !ext.isEmpty() && !popularExts.contains(ext.toLowerCase());
                    } else return false;
                })
                .sorted(Comparator.comparing(Language::getDisplayName))
                .map(TargetLanguage::new);

        Streams.concat(Stream.of(popular), otherRegistered)
         .forEach(targetLanguage::addItem);

        targetLanguage.setSelectedItem(selected);
    }


    protected void refreshTestInputPane() {
        if(testInputHolder.getComponentCount() > 0) {
            testInputHolder.remove(0);
        }
        final int classCount = getClassNumber();
        if(classCount > 1) {
            final JTabbedPane tabbedPane = new JBTabbedPane();
            inputClassIndices().forEach(i -> {
                final String className = "SomeClass" + i;
                tabbedPane.addTab(className, newTestInputSourceView(className).getComponent());
            });
            testInputHolder.add(tabbedPane);
        } else {
            testInputHolder.add(newTestInputSourceView("SomeClass").getComponent());
        }
    }

    private Editor newTestInputSourceView(String className) {
        final TestInput selectedTestInput = getSelectedTestInput();
        return Editors.createSourceEditor(null,
                selectedTestInput.getLanguage(),
                selectedTestInput.getSource(className),
                true);
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
                  Editors.createSourceEditor(null, "txt", error.getMessage(), true).getComponent());
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
        return editor.getDocument().getText();
    }

    public String getFileEncoding() {
        return fileEncodingText.getText();
    }

    public int getClassNumber() {
        return ((Number)classNumberSpinner.getValue()).intValue();
    }

    public String getTargetLanguage() {
        final Object selectedItem = targetLanguage.getSelectedItem();
        if(selectedItem == null) return "java";
        else return selectedItem.toString();
    }

    @Data
    @AllArgsConstructor
    private static class TargetLanguage {
        public final String name;
        public final String fileType;

        public String getFileType() {
            return fileType;
        }

        @Override
        public String toString() {
            return name;
        }

        TargetLanguage(Language lang) {
            this.name = lang.getDisplayName();
            this.fileType = lang.getAssociatedFileType() == null ? "" : lang.getAssociatedFileType().getDefaultExtension();
        }
    }
}
