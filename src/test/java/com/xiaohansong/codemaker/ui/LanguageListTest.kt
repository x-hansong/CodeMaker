package com.xiaohansong.codemaker.ui

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.collections.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk
import javax.swing.Icon

internal class LanguageListTest: StringSpec( {
    "should put popular languages at the beginning" {
        val langs = listOf(ideLanguage("Lang 1", "l1"))
        languageList(langs) shouldStartWith LanguageList.popularLanguges
    }

    "rest of registered language should be sorted by name" {
        val langs = listOf(
                ideLanguage("Lang 2", "l2"),
                ideLanguage("Lang 1", "l1"),
                ideLanguage("Lang 10", "l10")
        )
        val otherRegisteredLangs = languageListWithoutPopular(langs)
        otherRegisteredLangs.map {it.name } shouldContainExactlyInAnyOrder listOf("Lang 1", "Lang 10", "Lang 2")
    }

    "ignore registered langs with no associated type" {
        val langs = listOf(ideLanguage("Lang1", null))
        languageListWithoutPopular(langs) shouldHaveSize 0
    }

    "ignore registered langs with empty associated type" {
        val langs = listOf(ideLanguage("Lang1", ""))
        languageListWithoutPopular(langs) shouldHaveSize 0
    }

    "find should return default language if not found" {
        LanguageList().find("foo") shouldBe LanguageList.defaultLanguage
    }

    "should skip popular langs in ide registered langs" {
        val langs = listOf(ideLanguage("Kotlin", "kt"), ideLanguage("Scala", "scala"),
                ideLanguage("Lang1", "lg"))
        languageListWithoutPopular(langs) shouldContainExactly listOf(TargetLanguage("Lang1", "lg"))
    }

}) {
    companion object {
        fun ideLanguage(name: String, fileType: String?) =
                object : Language(name, true) {
                    override fun getAssociatedFileType(): LanguageFileType? {
                        return fileType?.let { DummyFileType(it) }
                    }
                }

        fun languageListWithoutPopular(ideLangs: Collection<Language>) =
                languageList(ideLangs).drop(LanguageList.popularLanguges.size)

        fun languageList(ideLangs: Collection<Language>) =
                LanguageList { ideLangs }.languages


        class DummyFileType(private val defaultExtension: String): LanguageFileType(mockk()) {
            override fun getDefaultExtension(): String = defaultExtension
            override fun getIcon(): Icon? = null
            override fun getCharset(file: VirtualFile, content: ByteArray): String? = "UTF8"
            override fun getName(): String = defaultExtension
            override fun getDescription(): String = defaultExtension
            override fun isReadOnly(): Boolean = false
        }
    }
}


