package com.stoic.actionscriptplugin.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
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
            settings,
            null
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
    private val settings: CodeStyleSettings,
    private val parentBlock: ActionScriptBlock?
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
                    settings,
                    this
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
        val text1 = child1.node.text
        val text2 = child2.node.text
        
        // Special handling for braces
        if (type1 == ActionScriptTokenTypes.LBRACE || type2 == ActionScriptTokenTypes.RBRACE) {
            // Always newline after { and before }
            if (type1 == ActionScriptTokenTypes.LBRACE) {
                return Spacing.createSpacing(0, 0, 1, true, 1)
            }
            if (type2 == ActionScriptTokenTypes.RBRACE) {
                // Check if previous token is also a brace (empty block)
                if (type1 == ActionScriptTokenTypes.LBRACE) {
                    return Spacing.createSpacing(0, 1, 0, false, 0)
                }
                return Spacing.createSpacing(0, 0, 1, true, 0)
            }
        }
        
        // Newline after semicolon when not in for loop
        if (type1 == ActionScriptTokenTypes.SEMICOLON) {
            // Check if we're inside parentheses (for loop)
            if (!isInsideParentheses()) {
                return Spacing.createSpacing(0, 0, 1, true, 1)
            } else {
                return Spacing.createSpacing(1, 1, 0, false, 0)
            }
        }
        
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
        
        // No space after dot
        if (type1 == ActionScriptTokenTypes.DOT) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }
        
        // No space between identifier and opening parenthesis (function call)
        if (type1 == ActionScriptTokenTypes.IDENTIFIER && type2 == ActionScriptTokenTypes.LPAREN) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }
        
        // No space after opening parenthesis or before closing parenthesis
        if (type1 == ActionScriptTokenTypes.LPAREN || type2 == ActionScriptTokenTypes.RPAREN) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }
        
        // Space around operators
        if (isOperator(type1) || isOperator(type2)) {
            // Special case for unary operators
            if (type1 == ActionScriptTokenTypes.NOT || 
                (type1 in setOf(ActionScriptTokenTypes.PLUS, ActionScriptTokenTypes.MINUS) && 
                 isUnaryContext(child1))) {
                return Spacing.createSpacing(0, 0, 0, false, 0)
            }
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // Space after keywords
        if (isKeyword(type1)) {
            // No space after keyword if followed by semicolon
            if (type2 == ActionScriptTokenTypes.SEMICOLON) {
                return Spacing.createSpacing(0, 0, 0, false, 0)
            }
            // Space after keyword
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // Space before opening brace
        if (type2 == ActionScriptTokenTypes.LBRACE) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }
        
        // Space after closing parenthesis if not followed by semicolon or opening brace
        if (type1 == ActionScriptTokenTypes.RPAREN) {
            if (type2 != ActionScriptTokenTypes.SEMICOLON && 
                type2 != ActionScriptTokenTypes.LBRACE &&
                type2 != ActionScriptTokenTypes.COMMA) {
                return Spacing.createSpacing(1, 1, 0, false, 0)
            }
        }
        
        // Default spacing between tokens
        return Spacing.createSpacing(1, 1, 0, false, 0)
    }

    override fun isLeaf(): Boolean = node.firstChildNode == null

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        // Handle indentation for new lines
        val type = node.elementType
        
        // If we're a block (between braces), indent children
        if (type == ActionScriptTokenTypes.LBRACE || 
            (node.text?.startsWith("{") == true)) {
            return ChildAttributes(Indent.getNormalIndent(), null)
        }
        
        // If we're after certain keywords, indent the next line
        if (isKeyword(type) && type != ActionScriptTokenTypes.ELSE) {
            return ChildAttributes(Indent.getNormalIndent(), null)
        }
        
        return ChildAttributes(Indent.getNoneIndent(), null)
    }

    private fun getChildIndent(child: ASTNode): Indent {
        val childType = child.elementType
        val childText = child.text
        
        // Never indent the closing brace itself
        if (childType == ActionScriptTokenTypes.RBRACE) {
            return Indent.getNoneIndent()
        }
        
        // Check if this child is inside a block (has a preceding LBRACE sibling)
        if (hasOpenBraceBefore(child)) {
            return Indent.getNormalIndent()
        }
        
        // Check if parent contains an opening brace
        if (node.text?.contains("{") == true && !childText.contains("}")) {
            // Find if child comes after the brace
            var current = node.firstChildNode
            var foundBrace = false
            while (current != null) {
                if (current.elementType == ActionScriptTokenTypes.LBRACE) {
                    foundBrace = true
                }
                if (current == child && foundBrace) {
                    return Indent.getNormalIndent()
                }
                current = current.treeNext
            }
        }
        
        return Indent.getNoneIndent()
    }
    
    private fun hasOpenBraceBefore(child: ASTNode): Boolean {
        var prev = child.treePrev
        var braceCount = 0
        
        while (prev != null) {
            when (prev.elementType) {
                ActionScriptTokenTypes.LBRACE -> braceCount++
                ActionScriptTokenTypes.RBRACE -> braceCount--
            }
            prev = prev.treePrev
        }
        
        return braceCount > 0
    }
    
    private fun isInsideParentheses(): Boolean {
        var current: ActionScriptBlock? = this
        while (current != null) {
            var checkNode = current.node
            var parenCount = 0
            var prev = checkNode.treePrev
            
            while (prev != null) {
                when (prev.elementType) {
                    ActionScriptTokenTypes.LPAREN -> parenCount++
                    ActionScriptTokenTypes.RPAREN -> parenCount--
                }
                prev = prev.treePrev
            }
            
            if (parenCount > 0) return true
            current = current.parentBlock
        }
        return false
    }
    
    private fun isUnaryContext(block: ActionScriptBlock): Boolean {
        val prev = block.node.treePrev
        if (prev == null) return true
        
        return when (prev.elementType) {
            ActionScriptTokenTypes.LPAREN,
            ActionScriptTokenTypes.COMMA,
            ActionScriptTokenTypes.EQUALS,
            ActionScriptTokenTypes.RETURN -> true
            else -> isOperator(prev.elementType)
        }
    }
    
    private fun isOperator(type: com.intellij.psi.tree.IElementType?): Boolean {
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
            ActionScriptTokenTypes.NOT,
            ActionScriptTokenTypes.QUESTION
        )
    }
    
    private fun isKeyword(type: com.intellij.psi.tree.IElementType?): Boolean {
        return type in ActionScriptTokenTypes.allKeywords
    }
}