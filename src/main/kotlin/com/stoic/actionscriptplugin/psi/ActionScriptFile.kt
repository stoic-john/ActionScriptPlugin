package com.stoic.actionscriptplugin.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.stoic.actionscriptplugin.ActionScriptFileType
import com.stoic.actionscriptplugin.ActionScriptLanguage

class ActionScriptFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ActionScriptLanguage.INSTANCE) {
    override fun getFileType(): FileType = ActionScriptFileType.INSTANCE
    override fun toString(): String = "ActionScript File"
}