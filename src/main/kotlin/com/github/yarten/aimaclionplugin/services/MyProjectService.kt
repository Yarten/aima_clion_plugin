package com.github.yarten.aimaclionplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.yarten.aimaclionplugin.MyBundle
import com.github.yarten.aimaclionplugin.core.downloadFromUrl
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.markdown.utils.convertMarkdownToHtml
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import fleet.rpc.core.retry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths

private const val KEY_GITLAB_TOKEN = "gitlab_token"


@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {
    // 插件数据的存储位置
    private val pluginHome : String =
        Paths
            .get(System.getProperty("user.home"), ".aima_clion_plugin")
            .toAbsolutePath()
            .toString()

    // 首页说明的文件。每次启动都会重新拉取加载
    private var homePageFile: VirtualFile? = null

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        File(pluginHome).mkdirs()
    }

    fun getRandomNumber() = (1..100).random()

    /**
     * 获取插件首页说明
     */
    suspend fun getHomePageContent(): String {
        if (homePageFile == null) {
            val homePageUrl = "https://raw.githubusercontent.com/Yarten/aima_clion_plugin/refs/heads/github/src/main/resources/data/HomePage.md"
            val targetFile = File(pluginHome, "HomePage.md")
            targetFile.downloadFromUrl(homePageUrl)
            homePageFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile)
        }

        return convertMarkdownToHtml(homePageFile!!.readText())
    }

    /**
     * 保存用户的 gitlab token
     */
    fun saveGitlabToken(token: String) {
        val attr = createCredentialAttributes(KEY_GITLAB_TOKEN)
        val cred = Credentials("gitlab", token)
        PasswordSafe.instance.set(attr, cred)
    }

    /**
     * 获取用户的 gitlab token
     */
    fun loadGitlabToken(): String {
        val attr = createCredentialAttributes(KEY_GITLAB_TOKEN)
        return PasswordSafe.instance.getPassword(attr).orEmpty()
    }

    /**
     * 生成指定的秘钥句柄
     */
    private fun createCredentialAttributes(key: String) =
        CredentialAttributes(generateServiceName(project.name, key))

    /**
     * 使用 github 的 api 来代替 gitlab api，测试获取仓库内容的过程
     */
    suspend fun testGetContentFromGithub(): String {
        val url = "https://api.github.com/repos/Yarten/aima_clion_plugin/contents/"
        val client = HttpClient()
        try {
            val response = client.get(url)
            return response.bodyAsText()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
        return ""
    }

    suspend fun getImageLists(): List<String> {
        val url = "https://api.github.com/repos/Yarten/aima_clion_plugin/contents/tree/github/src/main/resources/data/testImages"
        val client = HttpClient()
        try {
            val response = client.get(url)
            thisLogger().warn("get response: ${response.bodyAsText()}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
        return emptyList()
    }
}
