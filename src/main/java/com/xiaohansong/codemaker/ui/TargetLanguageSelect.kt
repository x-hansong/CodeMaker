package com.xiaohansong.codemaker.ui

import com.intellij.lang.Language
import javax.swing.JComboBox

object TargetLanguageSelect {
    private val languageList = LanguageList()
    @JvmStatic
    fun initCombo(langCombo: JComboBox<TargetLanguage>, selected: String) {
        // this sets the width of the combobox
        langCombo.prototypeDisplayValue = TargetLanguage("xxxxxxxxxxxxxx", "")
        LanguageList().languages.forEach { langCombo.addItem(it) }
        langCombo.selectedItem = languageList.find(selected)
    }
}

val JComboBox<TargetLanguage>.selectedLanguage: TargetLanguage
  get() = this.selectedItem as TargetLanguage


data class TargetLanguage(val name: String, val fileType: String) {
    override fun toString(): String {
        return name
    }
}

class LanguageList(registeredLanguages: () -> Collection<Language> = Language::getRegisteredLanguages) {

    val languages: List<TargetLanguage> by lazy {
        val popularTypes = popularLanguges.map { it.fileType }.toSet()
        popularLanguges + registeredLanguages()
                .filter {
                    it.fileType.isNotBlank() && !(it.fileType in popularTypes)
                }
                .sortedBy { it.displayName }
                .map { TargetLanguage(it.displayName, it.associatedFileType?.defaultExtension.orEmpty()) }
    }

    fun find(id: String): TargetLanguage =
            languages.find { it.fileType.toLowerCase() == id.toLowerCase() } ?: defaultLanguage



    companion object {
        val Language.fileType: String
            get() = this.associatedFileType?.defaultExtension.orEmpty()

        val defaultLanguage = TargetLanguage("Java", "java")

        val popularLanguges = listOf(
                defaultLanguage,
                TargetLanguage("Scala", "scala"),
                TargetLanguage("Kotlin", "kt"),
                TargetLanguage("JavaScript", "js"),
                TargetLanguage("TypeScript", "ts"),
                TargetLanguage("SQL", "sql"))
    }
}
