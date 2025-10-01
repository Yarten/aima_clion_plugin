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


private val TABS_CREATORS = arrayOf(
    ::homePage,
    ::homePage,
    ::homePage,
    ::homePage,
    ::testPanel,
)


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(
            myToolWindow.getContent(project), null, false
        )
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent1() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(MyBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(MyBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = MyBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }

        fun getContent(project: Project) = panel {
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
}
