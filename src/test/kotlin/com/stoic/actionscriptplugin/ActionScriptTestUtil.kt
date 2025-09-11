package com.stoic.actionscriptplugin

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

object ActionScriptTestUtil {
    
    fun createActionScriptFile(fixture: CodeInsightTestFixture, fileName: String, content: String): PsiFile {
        val factory = PsiFileFactory.getInstance(fixture.project)
        return factory.createFileFromText(
            fileName,
            ActionScriptFileType.INSTANCE,
            content
        )
    }
    
    fun formatAndAssert(fixture: CodeInsightTestFixture, input: String, expected: String) {
        val file = fixture.configureByText("test.as", input)
        fixture.performEditorAction("ReformatCode")
        val actual = file.text
        fixture.assertEquals("Formatting failed", expected, actual)
    }
}