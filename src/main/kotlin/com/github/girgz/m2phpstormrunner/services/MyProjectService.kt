package com.github.girgz.m2phpstormrunner.services

import com.intellij.openapi.project.Project
import com.github.girgz.m2phpstormrunner.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
