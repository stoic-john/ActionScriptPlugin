package com.stoic.actionscriptplugin.psi

import com.intellij.psi.tree.IElementType
import com.stoic.actionscriptplugin.ActionScriptLanguage

class ActionScriptTokenType(debugName: String) : IElementType(debugName, ActionScriptLanguage.INSTANCE) {
    override fun toString(): String = "ActionScriptTokenType." + super.toString()
}

class ActionScriptElementType(debugName: String) : IElementType(debugName, ActionScriptLanguage.INSTANCE)