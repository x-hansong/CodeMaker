package com.xiaohansong.codemaker.ui

import com.intellij.openapi.editor.Editor
import com.intellij.ui.components.JBTabbedPane
import com.xiaohansong.codemaker.TemplateLanguage
import com.xiaohansong.codemaker.templates.input.TestInput
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.SwingUtilities

class TemplateEditAndTest(private val splitPane: JSplitPane,
                          private val testInput: () -> TestInput,
                          private val classCount: () -> Int,
                          private val templateContent:  String,
                          private val templateLanguage: () -> TemplateLanguage,
                          private val dividerPosWhenShown: Int = 150
                         ) {
    var testInputShown = false
        private set

    private val testInputEditors = arrayListOf<Editor>()
    private var templateEditor: Editor = newTemplateEditor(templateContent, templateLanguage())

    private val testInputPanel: JPanel = splitPane.topComponent as JPanel
    private val templateEditPanel: JPanel = splitPane.bottomComponent as JPanel

    val templateText
        get() = templateEditor.document.text

    init {
        splitPane.dividerLocation = 0
        splitPane.isEnabled = false

        // when split pane is resized (layout is called) it may move the divider
        splitPane.addPropertyChangeListener("dividerLocation") { e ->
            if (!testInputShown && e.newValue as Int > 0) {
                SwingUtilities.invokeLater { splitPane.dividerLocation = 0 }
            }
        }

        refreshTestInput()
        refreshTemplateEditor()
    }

    fun toggleTestInputPane() {
        if(testInputShown) {
            hideTestInputPane()
        } else {
            showTestInputPane()
        }
    }

    fun refreshTemplateEditor() {
        if (templateEditPanel.componentCount > 0) {
            Editors.release(templateEditor)
            templateEditPanel.remove(0)
        }
        val editor = newTemplateEditor(templateText, templateLanguage())
        templateEditPanel.add(editor.component, BorderLayout.CENTER)
        templateEditor = editor
    }

    private fun newTemplateEditor(text: String, lang: TemplateLanguage): Editor {
        return Editors.createSourceEditor(null, lang.fileType, text, false)
    }

    fun refreshTestInput() {
        if (testInputPanel.componentCount > 0) {
            testInputEditors.forEach { Editors.release(it) }
            testInputEditors.clear()
            testInputPanel.remove(0)
        }
        val count = classCount()
        val input = testInput()
        if (count > 1) {
            val tabbedPane = JBTabbedPane()
            tabbedPane.insets.set(0, 0, 0, 0)
            (1..count).forEach { i ->
                val className = "SomeClass" + i
                val editor = newTestInputSourceView(input, className)
                tabbedPane.addTab(className, editor.getComponent())
                testInputEditors.add(editor)
            }
            testInputPanel.add(tabbedPane)
        } else {
            val editor = newTestInputSourceView(input, "SomeClass")
            testInputEditors.add(editor)
            testInputPanel.add(editor.component)
        }
    }

    private fun hideTestInputPane() {
        testInputShown = false
        splitPane.isEnabled = false
        splitPane.dividerLocation = 0
    }

    private fun showTestInputPane() {
        testInputShown = true
        splitPane.isEnabled = true
        splitPane.dividerLocation = dividerPosWhenShown
    }

    private fun newTestInputSourceView(testInput: TestInput, className: String): Editor {
        return Editors.createSourceEditor(null,
                testInput.language,
                testInput.getSource(className),
                true)
    }
}
