package com.stoic.actionscriptplugin

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class ActionScriptFileType : LanguageFileType(ActionScriptLanguage.Companion.INSTANCE) {
    companion object {
        val INSTANCE = ActionScriptFileType()
    }
    override fun getName() = "ActionScript"
    override fun getDescription() = "ActionScript files"
    override fun getDefaultExtension() = "as"
    override fun getIcon(): Icon? {
        return AllIcons.FileTypes.Text
    }
}