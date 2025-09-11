package com.stoic.actionscriptplugin.psi

import com.intellij.psi.TokenType

object ActionScriptTokenTypes {
    // Literals
    val STRING = ActionScriptTokenType("STRING")
    val NUMBER = ActionScriptTokenType("NUMBER")
    val IDENTIFIER = ActionScriptTokenType("IDENTIFIER")
    
    // Keywords
    val CLASS = ActionScriptTokenType("class")
    val FUNCTION = ActionScriptTokenType("function")
    val VAR = ActionScriptTokenType("var")
    val CONST = ActionScriptTokenType("const")
    val IF = ActionScriptTokenType("if")
    val ELSE = ActionScriptTokenType("else")
    val FOR = ActionScriptTokenType("for")
    val WHILE = ActionScriptTokenType("while")
    val DO = ActionScriptTokenType("do")
    val SWITCH = ActionScriptTokenType("switch")
    val CASE = ActionScriptTokenType("case")
    val DEFAULT = ActionScriptTokenType("default")
    val RETURN = ActionScriptTokenType("return")
    val BREAK = ActionScriptTokenType("break")
    val CONTINUE = ActionScriptTokenType("continue")
    val PUBLIC = ActionScriptTokenType("public")
    val PRIVATE = ActionScriptTokenType("private")
    val PROTECTED = ActionScriptTokenType("protected")
    val STATIC = ActionScriptTokenType("static")
    val FINAL = ActionScriptTokenType("final")
    val OVERRIDE = ActionScriptTokenType("override")
    val IMPORT = ActionScriptTokenType("import")
    val PACKAGE = ActionScriptTokenType("package")
    val EXTENDS = ActionScriptTokenType("extends")
    val IMPLEMENTS = ActionScriptTokenType("implements")
    val INTERFACE = ActionScriptTokenType("interface")
    val NEW = ActionScriptTokenType("new")
    val TRUE = ActionScriptTokenType("true")
    val FALSE = ActionScriptTokenType("false")
    val NULL = ActionScriptTokenType("null")
    val UNDEFINED = ActionScriptTokenType("undefined")
    val THIS = ActionScriptTokenType("this")
    val SUPER = ActionScriptTokenType("super")
    val TRY = ActionScriptTokenType("try")
    val CATCH = ActionScriptTokenType("catch")
    val FINALLY = ActionScriptTokenType("finally")
    val THROW = ActionScriptTokenType("throw")
    
    // Operators and punctuation
    val LBRACE = ActionScriptTokenType("{")
    val RBRACE = ActionScriptTokenType("}")
    val LBRACKET = ActionScriptTokenType("[")
    val RBRACKET = ActionScriptTokenType("]")
    val LPAREN = ActionScriptTokenType("(")
    val RPAREN = ActionScriptTokenType(")")
    val SEMICOLON = ActionScriptTokenType(";")
    val COMMA = ActionScriptTokenType(",")
    val DOT = ActionScriptTokenType(".")
    val COLON = ActionScriptTokenType(":")
    val EQUALS = ActionScriptTokenType("=")
    val PLUS = ActionScriptTokenType("+")
    val MINUS = ActionScriptTokenType("-")
    val MULT = ActionScriptTokenType("*")
    val DIV = ActionScriptTokenType("/")
    val LT = ActionScriptTokenType("<")
    val GT = ActionScriptTokenType(">")
    val LE = ActionScriptTokenType("<=")
    val GE = ActionScriptTokenType(">=")
    val EQ = ActionScriptTokenType("==")
    val NE = ActionScriptTokenType("!=")
    val AND = ActionScriptTokenType("&&")
    val OR = ActionScriptTokenType("||")
    val NOT = ActionScriptTokenType("!")
    val QUESTION = ActionScriptTokenType("?")
    
    // Comments
    val LINE_COMMENT = ActionScriptTokenType("LINE_COMMENT")
    val BLOCK_COMMENT = ActionScriptTokenType("BLOCK_COMMENT")
    
    // Whitespace
    val WHITE_SPACE = TokenType.WHITE_SPACE
    val BAD_CHARACTER = TokenType.BAD_CHARACTER
    
    val allKeywords = setOf(
        CLASS, FUNCTION, VAR, CONST, IF, ELSE, FOR, WHILE, DO, SWITCH, CASE, DEFAULT,
        RETURN, BREAK, CONTINUE, PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, OVERRIDE,
        IMPORT, PACKAGE, EXTENDS, IMPLEMENTS, INTERFACE, NEW, TRUE, FALSE, NULL,
        UNDEFINED, THIS, SUPER, TRY, CATCH, FINALLY, THROW
    )
}