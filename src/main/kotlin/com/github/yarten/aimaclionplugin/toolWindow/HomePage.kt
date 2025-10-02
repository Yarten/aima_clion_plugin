package com.github.yarten.aimaclionplugin.toolWindow

import com.github.yarten.aimaclionplugin.core.MyCoroutine
import com.github.yarten.aimaclionplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBHtmlPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.whenTextChangedFromUi
import javax.swing.SwingUtilities

@TabItem(name = "HomePage")
fun homePage(project: Project) = panel {
    val service = project.service<MyProjectService>()

    row("Gitlab Token: ") {
        passwordField()
            .applyToComponent {
                text = service.loadGitlabToken()
            }
            .whenTextChangedFromUi { gitlabToken ->
                service.saveGitlabToken(gitlabToken)
            }

        button("Test Token") {
            thisLogger().warn("Test Token: ${service.loadGitlabToken()}")
        }
    }

    separator()

    row {
        scrollCell(createContentPane(service)).align(Align.FILL)
    }.resizableRow()
}

/**
 * 创建从仓库拉取得到的首页 markdown 文件，并渲染为 html 控件返回
 */
private fun createContentPane(service: MyProjectService) = JBHtmlPane().apply {
    text = "<p>  loading ...  </p>"

    val panel = this

    object: MyCoroutine(10_000) {
        override suspend fun run() {
            val text = service.getHomePageContent()
            SwingUtilities.invokeLater {
                panel.text = text
            }
        }

        override suspend fun failOnce(tryNumber: Int, e: Exception) {
            SwingUtilities.invokeLater {
                panel.text =
                    """  <p> loading ... (try again: ${tryNumber}th) </p>
                         <p style="color: red; font-weight: bold;"> error: ${e.message} </p>
                    """.trimIndent()
            }
        }

        override suspend fun finalFail() {
            SwingUtilities.invokeLater {
                panel.text = "<p style=\"color: red; font-weight: bold;\">  failed to load !  </p>"
            }
        }
    }.launch()
}
