package com.stoic.actionscriptplugin.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import com.stoic.actionscriptplugin.psi.ActionScriptTokenTypes

class ActionScriptLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset = 0
    private var endOffset = 0
    private var position = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var currentToken: IElementType? = null
    
    private val keywords = mapOf(
        "class" to ActionScriptTokenTypes.CLASS,
        "function" to ActionScriptTokenTypes.FUNCTION,
        "var" to ActionScriptTokenTypes.VAR,
        "const" to ActionScriptTokenTypes.CONST,
        "if" to ActionScriptTokenTypes.IF,
        "else" to ActionScriptTokenTypes.ELSE,
        "for" to ActionScriptTokenTypes.FOR,
        "while" to ActionScriptTokenTypes.WHILE,
        "do" to ActionScriptTokenTypes.DO,
        "switch" to ActionScriptTokenTypes.SWITCH,
        "case" to ActionScriptTokenTypes.CASE,
        "default" to ActionScriptTokenTypes.DEFAULT,
        "return" to ActionScriptTokenTypes.RETURN,
        "break" to ActionScriptTokenTypes.BREAK,
        "continue" to ActionScriptTokenTypes.CONTINUE,
        "public" to ActionScriptTokenTypes.PUBLIC,
        "private" to ActionScriptTokenTypes.PRIVATE,
        "protected" to ActionScriptTokenTypes.PROTECTED,
        "static" to ActionScriptTokenTypes.STATIC,
        "final" to ActionScriptTokenTypes.FINAL,
        "override" to ActionScriptTokenTypes.OVERRIDE,
        "import" to ActionScriptTokenTypes.IMPORT,
        "package" to ActionScriptTokenTypes.PACKAGE,
        "extends" to ActionScriptTokenTypes.EXTENDS,
        "implements" to ActionScriptTokenTypes.IMPLEMENTS,
        "interface" to ActionScriptTokenTypes.INTERFACE,
        "new" to ActionScriptTokenTypes.NEW,
        "true" to ActionScriptTokenTypes.TRUE,
        "false" to ActionScriptTokenTypes.FALSE,
        "null" to ActionScriptTokenTypes.NULL,
        "undefined" to ActionScriptTokenTypes.UNDEFINED,
        "this" to ActionScriptTokenTypes.THIS,
        "super" to ActionScriptTokenTypes.SUPER,
        "try" to ActionScriptTokenTypes.TRY,
        "catch" to ActionScriptTokenTypes.CATCH,
        "finally" to ActionScriptTokenTypes.FINALLY,
        "throw" to ActionScriptTokenTypes.THROW
    )
    
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.position = startOffset
        advance()
    }
    
    override fun getState(): Int = 0
    
    override fun getTokenType(): IElementType? = currentToken
    
    override fun getTokenStart(): Int = tokenStart
    
    override fun getTokenEnd(): Int = tokenEnd
    
    override fun advance() {
        tokenStart = position
        
        if (position >= endOffset) {
            currentToken = null
            return
        }
        
        val char = buffer[position]
        
        when {
            char.isWhitespace() -> {
                while (position < endOffset && buffer[position].isWhitespace()) {
                    position++
                }
                tokenEnd = position
                currentToken = ActionScriptTokenTypes.WHITE_SPACE
            }
            
            char == '/' && position + 1 < endOffset -> {
                when (buffer[position + 1]) {
                    '/' -> {
                        while (position < endOffset && buffer[position] != '\n') {
                            position++
                        }
                        tokenEnd = position
                        currentToken = ActionScriptTokenTypes.LINE_COMMENT
                    }
                    '*' -> {
                        position += 2
                        while (position + 1 < endOffset) {
                            if (buffer[position] == '*' && buffer[position + 1] == '/') {
                                position += 2
                                break
                            }
                            position++
                        }
                        tokenEnd = position
                        currentToken = ActionScriptTokenTypes.BLOCK_COMMENT
                    }
                    else -> {
                        position++
                        tokenEnd = position
                        currentToken = ActionScriptTokenTypes.DIV
                    }
                }
            }
            
            char == '"' || char == '\'' -> {
                val quote = char
                position++
                while (position < endOffset && buffer[position] != quote) {
                    if (buffer[position] == '\\' && position + 1 < endOffset) {
                        position += 2
                    } else {
                        position++
                    }
                }
                if (position < endOffset) position++
                tokenEnd = position
                currentToken = ActionScriptTokenTypes.STRING
            }
            
            char.isDigit() -> {
                while (position < endOffset && (buffer[position].isDigit() || buffer[position] == '.')) {
                    position++
                }
                tokenEnd = position
                currentToken = ActionScriptTokenTypes.NUMBER
            }
            
            char.isLetter() || char == '_' -> {
                while (position < endOffset && (buffer[position].isLetterOrDigit() || buffer[position] == '_')) {
                    position++
                }
                tokenEnd = position
                val tokenText = buffer.subSequence(tokenStart, tokenEnd).toString()
                currentToken = keywords[tokenText] ?: ActionScriptTokenTypes.IDENTIFIER
            }
            
            else -> {
                currentToken = when (char) {
                    '{' -> { position++; ActionScriptTokenTypes.LBRACE }
                    '}' -> { position++; ActionScriptTokenTypes.RBRACE }
                    '[' -> { position++; ActionScriptTokenTypes.LBRACKET }
                    ']' -> { position++; ActionScriptTokenTypes.RBRACKET }
                    '(' -> { position++; ActionScriptTokenTypes.LPAREN }
                    ')' -> { position++; ActionScriptTokenTypes.RPAREN }
                    ';' -> { position++; ActionScriptTokenTypes.SEMICOLON }
                    ',' -> { position++; ActionScriptTokenTypes.COMMA }
                    '.' -> { position++; ActionScriptTokenTypes.DOT }
                    ':' -> { position++; ActionScriptTokenTypes.COLON }
                    '=' -> {
                        if (position + 1 < endOffset && buffer[position + 1] == '=') {
                            position += 2
                            ActionScriptTokenTypes.EQ
                        } else {
                            position++
                            ActionScriptTokenTypes.EQUALS
                        }
                    }
                    '+' -> { position++; ActionScriptTokenTypes.PLUS }
                    '-' -> { position++; ActionScriptTokenTypes.MINUS }
                    '*' -> { position++; ActionScriptTokenTypes.MULT }
                    '<' -> {
                        if (position + 1 < endOffset && buffer[position + 1] == '=') {
                            position += 2
                            ActionScriptTokenTypes.LE
                        } else {
                            position++
                            ActionScriptTokenTypes.LT
                        }
                    }
                    '>' -> {
                        if (position + 1 < endOffset && buffer[position + 1] == '=') {
                            position += 2
                            ActionScriptTokenTypes.GE
                        } else {
                            position++
                            ActionScriptTokenTypes.GT
                        }
                    }
                    '!' -> {
                        if (position + 1 < endOffset && buffer[position + 1] == '=') {
                            position += 2
                            ActionScriptTokenTypes.NE
                        } else {
                            position++
                            ActionScriptTokenTypes.NOT
                        }
                    }
                    '&' -> {
                        if (position + 1 < endOffset && buffer[position + 1] == '&') {
                            position += 2
                            ActionScriptTokenTypes.AND
                        } else {
                            position++
                            ActionScriptTokenTypes.BAD_CHARACTER
                        }
                    }
                    '|' -> {
                        if (position + 1 < endOffset && buffer[position + 1] == '|') {
                            position += 2
                            ActionScriptTokenTypes.OR
                        } else {
                            position++
                            ActionScriptTokenTypes.BAD_CHARACTER
                        }
                    }
                    '?' -> { position++; ActionScriptTokenTypes.QUESTION }
                    else -> {
                        position++
                        ActionScriptTokenTypes.BAD_CHARACTER
                    }
                }
                tokenEnd = position
            }
        }
    }
    
    override fun getBufferSequence(): CharSequence = buffer
    
    override fun getBufferEnd(): Int = endOffset
}