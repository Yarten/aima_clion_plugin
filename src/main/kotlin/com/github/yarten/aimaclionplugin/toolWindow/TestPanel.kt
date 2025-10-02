package com.github.yarten.aimaclionplugin.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel

@TabItem(name = "TestPanel")
fun testPanel(project: Project) = panel {
    row {
        scrollCell(manyRowsTexts())
            .align(Align.FILL)
    }.resizableRow()
}


private fun manyRowsTexts() = panel {
    for (i in 0..100) {
        row {
            text("Test $i")
        }
    }
}
