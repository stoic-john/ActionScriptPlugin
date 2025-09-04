package com.stoic.actionscriptplugin

import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerPosition
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

class ActionScriptSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "AS_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )

        val STRING = TextAttributesKey.createTextAttributesKey(
            "AS_STRING",
            DefaultLanguageHighlighterColors.STRING
        )

        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "AS_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        val NUMBER = TextAttributesKey.createTextAttributesKey(
            "AS_NUMBER",
            DefaultLanguageHighlighterColors.NUMBER
        )

        // ActionScript keywords
        private val keywords = setOf(
            "class", "function", "var", "const", "if", "else", "for", "while",
            "do", "switch", "case", "default", "return", "break", "continue",
            "public", "private", "protected", "static", "final", "override",
            "import", "package", "extends", "implements", "interface", "new",
            "true", "false", "null", "undefined", "this", "super", "try", "catch",
            "finally", "throw", "typeof", "instanceof", "delete", "void"
        )

        // Token types
        val KEYWORD_TOKEN = IElementType("AS_KEYWORD", null)
        val STRING_TOKEN = IElementType("AS_STRING", null)
        val COMMENT_TOKEN = IElementType("AS_COMMENT", null)
        val NUMBER_TOKEN = IElementType("AS_NUMBER", null)
    }

    override fun getHighlightingLexer(): Lexer {
        return object : Lexer() {
            private var buffer: CharSequence = ""
            private var startOffset = 0
            private var endOffset = 0
            private var position = 0
            private var tokenStart = 0
            private var tokenEnd = 0
            private var currentToken: IElementType? = null

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
                        currentToken = TokenType.WHITE_SPACE
                    }

                    char == '/' && position + 1 < endOffset && buffer[position + 1] == '/' -> {
                        while (position < endOffset && buffer[position] != '\n') {
                            position++
                        }
                        tokenEnd = position
                        currentToken = COMMENT_TOKEN
                    }

                    char == '"' -> {
                        position++ // Skip opening quote
                        while (position < endOffset && buffer[position] != '"') {
                            position++
                        }
                        if (position < endOffset) position++ // Skip closing quote
                        tokenEnd = position
                        currentToken = STRING_TOKEN
                    }

                    char.isDigit() -> {
                        while (position < endOffset && buffer[position].isDigit()) {
                            position++
                        }
                        tokenEnd = position
                        currentToken = NUMBER_TOKEN
                    }

                    char.isLetter() || char == '_' -> {
                        while (position < endOffset && (buffer[position].isLetterOrDigit() || buffer[position] == '_')) {
                            position++
                        }
                        tokenEnd = position
                        val tokenText = buffer.subSequence(tokenStart, tokenEnd).toString()
                        currentToken = if (keywords.contains(tokenText)) KEYWORD_TOKEN else TokenType.WHITE_SPACE
                    }

                    else -> {
                        position++
                        tokenEnd = position
                        currentToken = TokenType.WHITE_SPACE
                    }
                }
            }

            override fun getCurrentPosition(): LexerPosition {
                val currentPos = position
                return object : LexerPosition {
                    override fun getOffset(): Int = currentPos
                    override fun getState(): Int = 0
                }
            }

            override fun restore(position: LexerPosition) {
                this.position = position.offset
                advance()
            }

            override fun getBufferSequence(): CharSequence = buffer
            override fun getBufferEnd(): Int = endOffset
        }
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            KEYWORD_TOKEN -> arrayOf(KEYWORD)
            STRING_TOKEN -> arrayOf(STRING)
            COMMENT_TOKEN -> arrayOf(COMMENT)
            NUMBER_TOKEN -> arrayOf(NUMBER)
            else -> emptyArray()
        }
    }
}