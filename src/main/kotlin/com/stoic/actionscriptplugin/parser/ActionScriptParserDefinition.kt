package com.stoic.actionscriptplugin.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.stoic.actionscriptplugin.ActionScriptLanguage
import com.stoic.actionscriptplugin.lexer.ActionScriptLexer
import com.stoic.actionscriptplugin.psi.ActionScriptFile
import com.stoic.actionscriptplugin.psi.ActionScriptTokenTypes

class ActionScriptParserDefinition : ParserDefinition {
    
    companion object {
        val FILE = IFileElementType(ActionScriptLanguage.INSTANCE)
        
        val COMMENTS = TokenSet.create(
            ActionScriptTokenTypes.LINE_COMMENT,
            ActionScriptTokenTypes.BLOCK_COMMENT
        )
        
        val STRINGS = TokenSet.create(ActionScriptTokenTypes.STRING)
        
        val WHITESPACES = TokenSet.create(ActionScriptTokenTypes.WHITE_SPACE)
    }
    
    override fun createLexer(project: Project): Lexer = ActionScriptLexer()
    
    override fun createParser(project: Project): PsiParser = ActionScriptParser()
    
    override fun getFileNodeType(): IFileElementType = FILE
    
    override fun getCommentTokens(): TokenSet = COMMENTS
    
    override fun getStringLiteralElements(): TokenSet = STRINGS
    
    override fun getWhitespaceTokens(): TokenSet = WHITESPACES
    
    override fun createElement(node: ASTNode): PsiElement = ActionScriptPsiElement(node)
    
    override fun createFile(viewProvider: FileViewProvider): PsiFile = ActionScriptFile(viewProvider)
}