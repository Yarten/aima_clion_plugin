package com.github.yarten.aimaclionplugin.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.yarten.aimaclionplugin.MyBundle
import com.github.yarten.aimaclionplugin.services.MyProjectService
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.tabbedPaneHeader
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle.helpPage
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultSingleSelectionModel
import javax.swing.JButton
import kotlin.reflect.full.findAnnotation

/**
 * 定义了本插件展示的所有子页面内容。
 * 使用一个被 [TabItem] 修饰的、含有单个 [Project] 参数的函数来表示。
 */
private val TABS_CREATORS = arrayOf(
    ::homePage,
    ::homePage,
    ::homePage,
    ::homePage,
    ::testPanel,
)

class MyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(
            getContent(project), null, false
        )
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    /**
     * 创建插件顶层展示画面，是一个 tabs，其中页面由 [TABS_CREATORS] 定义。
     */
    private fun getContent(project: Project) = panel {
        val tabs: MutableList<Cell<*>> = mutableListOf()
        row {
            val header = tabbedPaneHeader(
                TABS_CREATORS.map { MyBundle.message(it.findAnnotation<TabItem>()!!.name) },
            ).apply { component.selectedIndex = 0 }

            header.component.model!!.addChangeListener {
                for ((index, tab) in tabs.withIndex()) {
                    tab.visible(index == (it.source as DefaultSingleSelectionModel).selectedIndex)
                }
            }
        }

        row {
            for ((index, tabCreator) in TABS_CREATORS.withIndex()) {
                val tab = scrollCell(tabCreator.call(project))
                    .visible(index == 0)
                    .align(Align.FILL)
                    .resizableColumn()
                tabs.add(tab)
            }
        }.resizableRow()
    }
}
