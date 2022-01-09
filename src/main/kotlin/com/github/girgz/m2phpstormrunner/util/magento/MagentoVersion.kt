package com.github.girgz.m2phpstormrunner.util.magento

import com.github.girgz.m2phpstormrunner.util.ComposerJson
import com.intellij.json.psi.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

class MagentoVersion {

    companion object {
        private lateinit var jsonObject: JsonObject

        fun get(project: Project, magentoPath: String): String? {
            val file: VirtualFile = LocalFileSystem.getInstance().findFileByPath(getComposerJsonPath(magentoPath)) ?: return null
            val psiManager = PsiManager.getInstance(project)
            val composerFile = psiManager.findFile(file)
            if (composerFile is JsonFile) {
                val jsonObject = PsiTreeUtil.getChildOfType(
                    composerFile,
                    JsonObject::class.java
                ) ?: return null

                this.jsonObject = jsonObject

                val composerJson = ComposerJson(jsonObject)
                return composerJson.getMagentoVersion()
            }
            return null
        }

        private fun getComposerJsonPath(magentoPath: String): String {
            return "$magentoPath/composer.json"
        }
    }
}