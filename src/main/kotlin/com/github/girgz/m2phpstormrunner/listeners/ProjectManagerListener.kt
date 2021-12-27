package com.github.girgz.m2phpstormrunner.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.github.girgz.m2phpstormrunner.services.MyProjectService
import com.github.girgz.m2phpstormrunner.util.magento.MagentoBasePathUtil
import com.github.girgz.m2phpstormrunner.util.magento.MagentoVersion
import com.intellij.ide.impl.ProjectUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.ui.Messages
import javax.swing.event.HyperlinkEvent

internal class ProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()
        val magentoFolderValid: Boolean = MagentoBasePathUtil.isMagentoFolderValid(project.basePath);
        val magentoVersion: String? = MagentoVersion.get(project, project.basePath!!)

        Messages.showMessageDialog(magentoVersion, "Info", Messages.getInformationIcon())
    }
}
