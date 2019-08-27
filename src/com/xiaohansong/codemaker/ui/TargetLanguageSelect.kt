package com.xiaohansong.codemaker.ui

import com.intellij.lang.Language
import javax.swing.JComboBox

object TargetLanguageSelect {
    fun initCombo(langCombo: JComboBox<TargetLanguage>, selected: String) = {
        // this sets the width of the combobox
        langCombo.prototypeDisplayValue = TargetLanguage("xxxxxxxxxxxxxx", "")
        LanguageList.languages.forEach {langCombo.addItem(it) }
        langCombo.selectedItem = LanguageList.find(selected)
    }
}

val JComboBox<TargetLanguage>.selectedLanguage: TargetLanguage
  get() = this.selectedItem as TargetLanguage


class TargetLanguage(val name: String, val fileType: String) {
    override fun toString(): String {
        return name
    }
}

object LanguageList {
    val Language.fileType: String
        get() = this.associatedFileType?.defaultExtension.orEmpty()

    val languages: List<TargetLanguage>
    val defaultLanguage = TargetLanguage("Java", "java")

    fun find(id: String): TargetLanguage =
        languages.find { it.fileType.toLowerCase() == id.toLowerCase() } ?: defaultLanguage

    init {
        val popular = listOf(
                defaultLanguage,
                TargetLanguage("Scala", "scala"),
                TargetLanguage("Kotlin", "kt"),
                TargetLanguage("JavaScript", "js"),
                TargetLanguage("TypeScript", "ts"),
                TargetLanguage("SQL", "sql"))

        val popularTypes = popular.map { it.fileType }.toSet()

        languages = popular + Language.getRegisteredLanguages()
                .filter {
                    it.fileType.isNotBlank() && !(it.fileType in popularTypes)
                }
                .sortedBy { it.displayName }
                .map { TargetLanguage(it.displayName, it.associatedFileType?.defaultExtension.orEmpty()) }
    }
}
