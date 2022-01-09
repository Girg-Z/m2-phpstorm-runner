package com.github.girgz.m2phpstormrunner.listeners

import com.github.girgz.m2phpstormrunner.Settings
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.github.girgz.m2phpstormrunner.services.MyProjectService
import com.github.girgz.m2phpstormrunner.util.magento.MagentoBasePathUtil
import com.github.girgz.m2phpstormrunner.util.magento.MagentoVersion

internal class ProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()
        val magentoFolderValid: Boolean = MagentoBasePathUtil.isMagentoFolderValid(project.basePath)
        val magentoVersion: String? = MagentoVersion.get(project, project.basePath!!)

        val settings = Settings.getInstance()

        // Cool! https://kotlinlang.org/docs/scope-functions.html
        magentoVersion?.let { settings.setMagentoVersion(it) }

        // Test will fail here
        // Messages.showMessageDialog(magentoVersion, "Info", Messages.getInformationIcon())
    }
}
