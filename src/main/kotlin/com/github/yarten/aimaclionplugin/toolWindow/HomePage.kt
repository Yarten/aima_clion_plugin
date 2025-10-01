package com.github.yarten.aimaclionplugin.toolWindow

import com.github.yarten.aimaclionplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.profile.codeInspection.ui.readHTML
import com.intellij.ui.components.JBHtmlPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.jcef.JCEFHtmlPanel
import javax.swing.JEditorPane
import javax.swing.JPanel

@TabItem(name = "HomePage")
fun homePage(project: Project) = panel {
    row {
        cell(
            JBHtmlPane().apply {
                val service = project.service<MyProjectService>()
                text = service.getHomePageContent()
            }
        )
    }
}
