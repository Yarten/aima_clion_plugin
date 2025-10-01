package com.github.yarten.aimaclionplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.yarten.aimaclionplugin.MyBundle
import com.github.yarten.aimaclionplugin.core.downloadFromUrl
import com.intellij.markdown.utils.convertMarkdownToHtml
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import java.nio.file.Paths

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
    // 插件数据的存储位置
    private val pluginHome : String =
        Paths
            .get(System.getProperty("user.home"), ".aima_clion_plugin")
            .toAbsolutePath()
            .toString()

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getRandomNumber() = (1..100).random()

    /**
     * 获取插件首页说明
     */
    fun getHomePageContent(): String {
        val homePageUrl = "https://raw.githubusercontent.com/Yarten/aima_clion_plugin/refs/heads/github/src/main/resources/data/HomePage.md"
        val targetFile = File(pluginHome, "HomePage.md")

        if (!targetFile.exists()) {
            targetFile.parentFile.mkdirs()
            targetFile.downloadFromUrl(homePageUrl)
            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile)
        }

        return convertMarkdownToHtml(targetFile.readText())
    }
}
