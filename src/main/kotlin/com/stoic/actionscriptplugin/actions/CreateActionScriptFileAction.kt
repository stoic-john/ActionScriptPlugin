package com.stoic.actionscriptplugin.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.stoic.actionscriptplugin.ActionScriptIcons

class CreateActionScriptFileAction : CreateFileFromTemplateAction(
    "ActionScript File",
    "Create new ActionScript file",
    ActionScriptIcons.File16
) {
    
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("New ActionScript File")
            .addKind("ActionScript Class", ActionScriptIcons.File16, "ActionScript Class")
    }
    
    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create ActionScript File: $newName"
    }
}