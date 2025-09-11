package com.stoic.actionscriptplugin.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingMode
import com.intellij.formatting.FormattingRangesInfo
import com.intellij.lang.ASTNode
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.stoic.actionscriptplugin.ActionScriptFileType
import com.stoic.actionscriptplugin.ActionScriptLanguage

class ActionScriptFormatterTest : BasePlatformTestCase() {

    fun testBasicClassFormatting() {
        val input = """
            |class A {
            |    var b: int;
            |}
        """.trimMargin()

        val expected = """
            |class A {
            |    var b: int;
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testClassWithModifiers() {
        val input = """
            |public class Blast {
            |public function Blast() {
            |// Constructor
            |}
            |public function Two() {
            |}
            |}
        """.trimMargin()

        val expected = """
            |public class Blast {
            |    
            |    public function Blast() {
            |        // Constructor
            |    }
            |    
            |    public function Two() {
            |    }
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testPackageWithMultipleClasses() {
        val input = """
            |package {
            |public class Blast {
            |public function Blast() {
            |// Constructor
            |}
            |public function Two() {
            |}
            |}
            |public class Ast {
            |}
            |}
        """.trimMargin()

        val expected = """
            |package {
            |    
            |    public class Blast {
            |        
            |        public function Blast() {
            |            // Constructor
            |        }
            |        
            |        public function Two() {
            |        }
            |    }
            |    
            |    public class Ast {
            |    }
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testVariableDeclarations() {
        val input = """
            |class Test {
            |var a:int=5;
            |const b:String="hello";
            |private var c:Boolean=true;
            |}
        """.trimMargin()

        val expected = """
            |class Test {
            |    var a: int = 5;
            |    const b: String = "hello";
            |    private var c: Boolean = true;
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testControlStructures() {
        val input = """
            |function test() {
            |if(condition){
            |doSomething();
            |}else{
            |doOther();
            |}
            |for(var i:int=0;i<10;i++){
            |trace(i);
            |}
            |}
        """.trimMargin()

        val expected = """
            |function test() {
            |    if (condition) {
            |        doSomething();
            |    } else {
            |        doOther();
            |    }
            |    for (var i: int = 0; i < 10; i++) {
            |        trace(i);
            |    }
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testNestedBlocks() {
        val input = """
            |class Outer {
            |function method() {
            |if(true) {
            |var x:int=1;
            |if(x>0) {
            |trace("positive");
            |}
            |}
            |}
            |}
        """.trimMargin()

        val expected = """
            |class Outer {
            |    function method() {
            |        if (true) {
            |            var x: int = 1;
            |            if (x > 0) {
            |                trace("positive");
            |            }
            |        }
            |    }
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testEmptyBlocks() {
        val input = """
            |class Empty {
            |function empty() {
            |}
            |function another() {
            |
            |}
            |}
        """.trimMargin()

        val expected = """
            |class Empty {
            |    function empty() {
            |    }
            |    
            |    function another() {
            |    }
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    fun testComments() {
        val input = """
            |class CommentTest {
            |// Line comment
            |function test() {
            |/* Block comment */
            |var x:int=1;// End line comment
            |}
            |}
        """.trimMargin()

        val expected = """
            |class CommentTest {
            |    // Line comment
            |    function test() {
            |        /* Block comment */
            |        var x: int = 1; // End line comment
            |    }
            |}
        """.trimMargin()

        assertFormatting(input, expected)
    }

    private fun assertFormatting(input: String, expected: String) {
        val psiFile = createActionScriptFile("test.as", input)
        
        WriteCommandAction.runWriteCommandAction(project) {
            val codeStyleManager = CodeStyleManager.getInstance(project)
            codeStyleManager.reformat(psiFile)
        }
        
        val actual = psiFile.text
        assertEquals("Formatting failed", expected, actual)
    }

    private fun createActionScriptFile(fileName: String, content: String): PsiFile {
        val factory = PsiFileFactory.getInstance(project)
        return factory.createFileFromText(
            fileName,
            ActionScriptFileType.INSTANCE,
            content
        )
    }

    override fun getTestDataPath(): String {
        return ""
    }
}