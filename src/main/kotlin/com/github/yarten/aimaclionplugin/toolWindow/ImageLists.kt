package com.github.yarten.aimaclionplugin.toolWindow

import com.github.yarten.aimaclionplugin.services.MyProjectService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.actionsButton
import com.intellij.ui.dsl.builder.panel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

@TabItem(name = "ImageLists")
fun imageLists(project: Project) = panel {
    val service = project.service<MyProjectService>()

    row("test") {
        button("Get Content") {
            CoroutineScope(Dispatchers.IO).launch {
                val result = service.testGetContentFromGithub()
                thisLogger().warn("Get Content: $result")
            }

            thisLogger().warn("Get Content After Launch")
        }
    }

    val model = DefaultTableModel(arrayOf("Name"), 0)
    lateinit var table: JTable

    row {
        actionButton(
            object: DumbAwareAction("Refresh", "", AllIcons.Actions.Refresh) {
                override fun actionPerformed(e: AnActionEvent) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
