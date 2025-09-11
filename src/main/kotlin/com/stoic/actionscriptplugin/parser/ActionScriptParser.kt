package com.stoic.actionscriptplugin.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.stoic.actionscriptplugin.psi.ActionScriptTokenTypes

class ActionScriptParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val marker = builder.mark()
        
        while (!builder.eof()) {
            if (!parseStatement(builder)) {
                builder.advanceLexer()
            }
        }
        
        marker.done(root)
        return builder.treeBuilt
    }
    
    private fun parseStatement(builder: PsiBuilder): Boolean {
        val tokenType = builder.tokenType
        
        return when (tokenType) {
            ActionScriptTokenTypes.PACKAGE -> parsePackage(builder)
            ActionScriptTokenTypes.IMPORT -> parseImport(builder)
            ActionScriptTokenTypes.CLASS -> parseClass(builder)
            ActionScriptTokenTypes.INTERFACE -> parseInterface(builder)
            ActionScriptTokenTypes.FUNCTION -> parseFunction(builder)
            ActionScriptTokenTypes.VAR, ActionScriptTokenTypes.CONST -> parseVariable(builder)
            ActionScriptTokenTypes.IF -> parseIf(builder)
            ActionScriptTokenTypes.FOR -> parseFor(builder)
            ActionScriptTokenTypes.WHILE -> parseWhile(builder)
            ActionScriptTokenTypes.DO -> parseDoWhile(builder)
            ActionScriptTokenTypes.SWITCH -> parseSwitch(builder)
            ActionScriptTokenTypes.TRY -> parseTry(builder)
            ActionScriptTokenTypes.RETURN -> parseReturn(builder)
            ActionScriptTokenTypes.THROW -> parseThrow(builder)
            ActionScriptTokenTypes.BREAK, ActionScriptTokenTypes.CONTINUE -> parseBreakContinue(builder)
            else -> false
        }
    }
    
    private fun parsePackage(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'package'
        
        // Parse package name
        while (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.DOT) {
                builder.advanceLexer()
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            parseBlock(builder)
        }
        
        marker.done(ActionScriptTokenTypes.PACKAGE)
        return true
    }
    
    private fun parseImport(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'import'
        
        while (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.DOT) {
                builder.advanceLexer()
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.MULT) {
            builder.advanceLexer() // consume '*'
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
            builder.advanceLexer()
        }
        
        marker.done(ActionScriptTokenTypes.IMPORT)
        return true
    }
    
    private fun parseClass(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        
        // Parse modifiers
        while (builder.tokenType in setOf(
            ActionScriptTokenTypes.PUBLIC,
            ActionScriptTokenTypes.PRIVATE,
            ActionScriptTokenTypes.PROTECTED,
            ActionScriptTokenTypes.STATIC,
            ActionScriptTokenTypes.FINAL
        )) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.CLASS) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.EXTENDS) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                builder.advanceLexer()
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.IMPLEMENTS) {
            builder.advanceLexer()
            while (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                builder.advanceLexer()
                if (builder.tokenType == ActionScriptTokenTypes.COMMA) {
                    builder.advanceLexer()
                }
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            parseBlock(builder)
        }
        
        marker.done(ActionScriptTokenTypes.CLASS)
        return true
    }
    
    private fun parseInterface(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'interface'
        
        if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            parseBlock(builder)
        }
        
        marker.done(ActionScriptTokenTypes.INTERFACE)
        return true
    }
    
    private fun parseFunction(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        
        // Parse modifiers
        while (builder.tokenType in setOf(
            ActionScriptTokenTypes.PUBLIC,
            ActionScriptTokenTypes.PRIVATE,
            ActionScriptTokenTypes.PROTECTED,
            ActionScriptTokenTypes.STATIC,
            ActionScriptTokenTypes.OVERRIDE
        )) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.FUNCTION) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            parseParameters(builder)
        }
        
        // Parse return type
        if (builder.tokenType == ActionScriptTokenTypes.COLON) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                builder.advanceLexer()
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            parseBlock(builder)
        }
        
        marker.done(ActionScriptTokenTypes.FUNCTION)
        return true
    }
    
    private fun parseVariable(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        
        builder.advanceLexer() // consume 'var' or 'const'
        
        if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.COLON) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                builder.advanceLexer()
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.EQUALS) {
            builder.advanceLexer()
            parseExpression(builder)
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
            builder.advanceLexer()
        }
        
        marker.done(ActionScriptTokenTypes.VAR)
        return true
    }
    
    private fun parseIf(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'if'
        
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            parseParenthesizedExpression(builder)
        }
        
        parseStatementOrBlock(builder)
        
        if (builder.tokenType == ActionScriptTokenTypes.ELSE) {
            builder.advanceLexer()
            parseStatementOrBlock(builder)
        }
        
        marker.done(ActionScriptTokenTypes.IF)
        return true
    }
    
    private fun parseFor(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'for'
        
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            builder.advanceLexer()
            
            // Parse init
            if (builder.tokenType != ActionScriptTokenTypes.SEMICOLON) {
                parseExpression(builder)
            }
            if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
                builder.advanceLexer()
            }
            
            // Parse condition
            if (builder.tokenType != ActionScriptTokenTypes.SEMICOLON) {
                parseExpression(builder)
            }
            if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
                builder.advanceLexer()
            }
            
            // Parse update
            if (builder.tokenType != ActionScriptTokenTypes.RPAREN) {
                parseExpression(builder)
            }
            
            if (builder.tokenType == ActionScriptTokenTypes.RPAREN) {
                builder.advanceLexer()
            }
        }
        
        parseStatementOrBlock(builder)
        
        marker.done(ActionScriptTokenTypes.FOR)
        return true
    }
    
    private fun parseWhile(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'while'
        
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            parseParenthesizedExpression(builder)
        }
        
        parseStatementOrBlock(builder)
        
        marker.done(ActionScriptTokenTypes.WHILE)
        return true
    }
    
    private fun parseDoWhile(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'do'
        
        parseStatementOrBlock(builder)
        
        if (builder.tokenType == ActionScriptTokenTypes.WHILE) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
                parseParenthesizedExpression(builder)
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
            builder.advanceLexer()
        }
        
        marker.done(ActionScriptTokenTypes.DO)
        return true
    }
    
    private fun parseSwitch(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'switch'
        
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            parseParenthesizedExpression(builder)
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            builder.advanceLexer()
            
            while (builder.tokenType in setOf(ActionScriptTokenTypes.CASE, ActionScriptTokenTypes.DEFAULT)) {
                if (builder.tokenType == ActionScriptTokenTypes.CASE) {
                    builder.advanceLexer()
                    parseExpression(builder)
                } else {
                    builder.advanceLexer() // consume 'default'
                }
                
                if (builder.tokenType == ActionScriptTokenTypes.COLON) {
                    builder.advanceLexer()
                }
                
                while (builder.tokenType != null && 
                       builder.tokenType !in setOf(
                           ActionScriptTokenTypes.CASE, 
                           ActionScriptTokenTypes.DEFAULT, 
                           ActionScriptTokenTypes.RBRACE
                       )) {
                    if (!parseStatement(builder)) {
                        builder.advanceLexer()
                    }
                }
            }
            
            if (builder.tokenType == ActionScriptTokenTypes.RBRACE) {
                builder.advanceLexer()
            }
        }
        
        marker.done(ActionScriptTokenTypes.SWITCH)
        return true
    }
    
    private fun parseTry(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'try'
        
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            parseBlock(builder)
        }
        
        while (builder.tokenType == ActionScriptTokenTypes.CATCH) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
                builder.advanceLexer()
                if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                    builder.advanceLexer()
                    if (builder.tokenType == ActionScriptTokenTypes.COLON) {
                        builder.advanceLexer()
                        if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                            builder.advanceLexer()
                        }
                    }
                }
                if (builder.tokenType == ActionScriptTokenTypes.RPAREN) {
                    builder.advanceLexer()
                }
            }
            if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
                parseBlock(builder)
            }
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.FINALLY) {
            builder.advanceLexer()
            if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
                parseBlock(builder)
            }
        }
        
        marker.done(ActionScriptTokenTypes.TRY)
        return true
    }
    
    private fun parseReturn(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'return'
        
        if (builder.tokenType != ActionScriptTokenTypes.SEMICOLON) {
            parseExpression(builder)
        }
        
        if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
            builder.advanceLexer()
        }
        
        marker.done(ActionScriptTokenTypes.RETURN)
        return true
    }
    
    private fun parseThrow(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'throw'
        
        parseExpression(builder)
        
        if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
            builder.advanceLexer()
        }
        
        marker.done(ActionScriptTokenTypes.THROW)
        return true
    }
    
    private fun parseBreakContinue(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer() // consume 'break' or 'continue'
        
        if (builder.tokenType == ActionScriptTokenTypes.SEMICOLON) {
            builder.advanceLexer()
        }
        
        marker.done(builder.tokenType ?: ActionScriptTokenTypes.BREAK)
        return true
    }
    
    private fun parseBlock(builder: PsiBuilder) {
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            builder.advanceLexer()
            
            while (builder.tokenType != ActionScriptTokenTypes.RBRACE && !builder.eof()) {
                if (!parseStatement(builder)) {
                    builder.advanceLexer()
                }
            }
            
            if (builder.tokenType == ActionScriptTokenTypes.RBRACE) {
                builder.advanceLexer()
            }
        }
    }
    
    private fun parseStatementOrBlock(builder: PsiBuilder) {
        if (builder.tokenType == ActionScriptTokenTypes.LBRACE) {
            parseBlock(builder)
        } else {
            parseStatement(builder)
        }
    }
    
    private fun parseParameters(builder: PsiBuilder) {
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            builder.advanceLexer()
            
            while (builder.tokenType != ActionScriptTokenTypes.RPAREN && !builder.eof()) {
                if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                    builder.advanceLexer()
                    
                    if (builder.tokenType == ActionScriptTokenTypes.COLON) {
                        builder.advanceLexer()
                        if (builder.tokenType == ActionScriptTokenTypes.IDENTIFIER) {
                            builder.advanceLexer()
                        }
                    }
                }
                
                if (builder.tokenType == ActionScriptTokenTypes.COMMA) {
                    builder.advanceLexer()
                } else if (builder.tokenType != ActionScriptTokenTypes.RPAREN) {
                    builder.advanceLexer()
                }
            }
            
            if (builder.tokenType == ActionScriptTokenTypes.RPAREN) {
                builder.advanceLexer()
            }
        }
    }
    
    private fun parseParenthesizedExpression(builder: PsiBuilder) {
        if (builder.tokenType == ActionScriptTokenTypes.LPAREN) {
            builder.advanceLexer()
            parseExpression(builder)
            if (builder.tokenType == ActionScriptTokenTypes.RPAREN) {
                builder.advanceLexer()
            }
        }
    }
    
    private fun parseExpression(builder: PsiBuilder) {
        // Simplified expression parsing
        var depth = 0
        while (!builder.eof()) {
            when (builder.tokenType) {
                ActionScriptTokenTypes.LPAREN, ActionScriptTokenTypes.LBRACKET, ActionScriptTokenTypes.LBRACE -> {
                    depth++
                    builder.advanceLexer()
                }
                ActionScriptTokenTypes.RPAREN, ActionScriptTokenTypes.RBRACKET, ActionScriptTokenTypes.RBRACE -> {
                    if (depth == 0) return
                    depth--
                    builder.advanceLexer()
                }
                ActionScriptTokenTypes.SEMICOLON -> {
                    if (depth == 0) return
                    builder.advanceLexer()
                }
                ActionScriptTokenTypes.COMMA -> {
                    if (depth == 0) return
                    builder.advanceLexer()
                }
                else -> builder.advanceLexer()
            }
        }
    }
}