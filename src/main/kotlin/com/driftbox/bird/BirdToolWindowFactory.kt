package com.driftbox.bird

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class BirdToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val gamePanel = BirdGamePanel()
        val content = ContentFactory.getInstance().createContent(gamePanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
