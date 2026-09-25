package com.driftbox.bird

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingConstants

class BirdToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val component = createGamePanel()
        val content = ContentFactory.getInstance().createContent(component, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createGamePanel(): JComponent {
        return try {
            val browser = JBCefBrowser()
            val gameUrl = javaClass.getResource("/game/index.html")
            if (gameUrl != null) {
                browser.loadURL(gameUrl.toExternalForm())
            } else {
                browser.loadHTML(FALLBACK_HTML)
            }
            browser.component
        } catch (e: Exception) {
            JLabel(
                "<html><center>JCEF not available.<br>The game requires a JetBrains runtime with JCEF support.</center></html>",
                SwingConstants.CENTER
            )
        }
    }

    companion object {
        private const val FALLBACK_HTML = """
            <html><body style="background:#1e1e1e;color:#888;font-family:sans-serif;display:flex;align-items:center;justify-content:center;height:100vh">
            <p>Could not load game resources.</p>
            </body></html>
        """
    }
}
