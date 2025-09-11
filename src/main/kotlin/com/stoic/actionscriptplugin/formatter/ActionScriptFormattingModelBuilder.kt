package com.stoic.actionscriptplugin.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock
import com.stoic.actionscriptplugin.psi.ActionScriptTokenTypes

class ActionScriptFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val element = formattingContext.psiElement
        val block = ActionScriptBlock(
            element.node,
            null,
            null,
            Indent.getNoneIndent(),
            settings
        )
        return FormattingModelProvider.createFormattingModelForPsiFile(
            element.containingFile,
            block,
            settings
        )
    }
}

class ActionScriptBlock(
    private val node: ASTNode,
    private val wrap: Wrap?,
    private val alignment: Alignment?,
    private val indent: Indent,
    private val settings: CodeStyleSettings
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        var child = node.firstChildNode
        
        while (child != null) {
            if (child.elementType != ActionScriptTokenTypes.WHITE_SPACE) {
                blocks.add(ActionScriptBlock(
                    child,
                    null,
                    null,
                    getChildIndent(child),
                    settings
                ))
            }
            child = child.treeNext
        }
        
        return blocks
    }

    override fun getIndent(): Indent = indent

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        if (child1 !is ActionScriptBlock || child2 !is ActionScriptBlock) {
            return null
        }
        
        val type1 = child1.node.elementType
        val type2 = child2.node.elementType
        
        // No space before semicolon, comma, dot
        if (type2 == ActionScriptTokenTypes.SEMICOLON ||
            type2 == ActionScriptTokenTypes.COMMA ||
            type2 == ActionScriptTokenTypes.DOT) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }
        
        // Space after comma
        if (type1 == ActionScriptTokenTypes.COMMA) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // No space after dot or before/after parentheses in function calls
        if (type1 == ActionScriptTokenTypes.DOT ||
            (type1 == ActionScriptTokenTypes.IDENTIFIER && type2 == ActionScriptTokenTypes.LPAREN)) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }
        
        // Space around operators
        if (isOperator(type1) || isOperator(type2)) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // Space after keywords
        if (isKeyword(type1) && type2 != ActionScriptTokenTypes.SEMICOLON) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // Space before opening brace
        if (type2 == ActionScriptTokenTypes.LBRACE) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // New line after opening brace and before closing brace
        if (type1 == ActionScriptTokenTypes.LBRACE) {
            return Spacing.createSpacing(0, 0, 1, true, 0)
        }
        if (type2 == ActionScriptTokenTypes.RBRACE) {
            return Spacing.createSpacing(0, 0, 1, true, 0)
        }
        
        // Default spacing
        return Spacing.createSpacing(0, 1, 0, false, 0)
    }

    override fun isLeaf(): Boolean = node.firstChildNode == null

    private fun getChildIndent(child: ASTNode): Indent {
        val parentType = node.elementType
        val childType = child.elementType
        
        // Indent block content
        if (parentType == ActionScriptTokenTypes.LBRACE && 
            childType != ActionScriptTokenTypes.RBRACE) {
            return Indent.getNormalIndent()
        }
        
        // Indent after certain keywords
        if (node.text in setOf("if", "else", "for", "while", "do", "switch", "try", "catch", "finally")) {
            if (childType != ActionScriptTokenTypes.LBRACE && 
                childType != ActionScriptTokenTypes.SEMICOLON) {
                return Indent.getNormalIndent()
            }
        }
        
        // Indent case content
        if (parentType == ActionScriptTokenTypes.CASE || 
            parentType == ActionScriptTokenTypes.DEFAULT) {
            return Indent.getNormalIndent()
        }
        
        return Indent.getNoneIndent()
    }
    
    private fun isOperator(type: com.intellij.psi.tree.IElementType): Boolean {
        return type in setOf(
            ActionScriptTokenTypes.EQUALS,
            ActionScriptTokenTypes.PLUS,
            ActionScriptTokenTypes.MINUS,
            ActionScriptTokenTypes.MULT,
            ActionScriptTokenTypes.DIV,
            ActionScriptTokenTypes.LT,
            ActionScriptTokenTypes.GT,
            ActionScriptTokenTypes.LE,
            ActionScriptTokenTypes.GE,
            ActionScriptTokenTypes.EQ,
            ActionScriptTokenTypes.NE,
            ActionScriptTokenTypes.AND,
            ActionScriptTokenTypes.OR,
            ActionScriptTokenTypes.NOT
        )
    }
    
    private fun isKeyword(type: com.intellij.psi.tree.IElementType): Boolean {
        return type in ActionScriptTokenTypes.allKeywords
    }
}