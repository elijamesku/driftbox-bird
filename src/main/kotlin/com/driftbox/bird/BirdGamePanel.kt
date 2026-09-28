package com.driftbox.bird

import java.awt.*
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.awt.geom.Ellipse2D
import javax.swing.JPanel
import javax.swing.Timer

class BirdGamePanel : JPanel(), ActionListener, KeyListener, MouseListener {

    private val VW = 320
    private val VH = 480

    private val GRAVITY = 0.5
    private val JUMP = -7.5
    private val PIPE_SPEED = 3.5
    private val GAP = 130
    private val PW = 45
    private val BIRD_SIZE = 20
    private val GROUND_H = 40

    private var state = State.IDLE
    private var birdY = 200.0
    private var birdV = 0.0
    private var pipes = mutableListOf<Pipe>()
    private var frame = 0
    private var score = 0
    private var highScore = 0

    private val timer = Timer(16, this)

    private val clouds = listOf(
        Cloud(50.0, 60.0, 25, 30, 25),
        Cloud(180.0, 90.0, 20, 25, 20),
        Cloud(300.0, 50.0, 22, 28, 22),
        Cloud(420.0, 110.0, 18, 24, 18)
    )

    private val buildings = listOf(
        Building(0, 40, 80), Building(50, 30, 60), Building(90, 50, 100),
        Building(150, 35, 70), Building(195, 45, 90), Building(250, 30, 55),
        Building(290, 55, 85), Building(355, 40, 75), Building(405, 35, 65),
        Building(450, 50, 95)
    )

    init {
        isFocusable = true
        addKeyListener(this)
        addMouseListener(this)
        timer.start()
    }

    private fun reset() {
        birdY = 200.0
        birdV = 0.0
        pipes.clear()
        frame = 0
        score = 0
    }

    private fun jump() {
        requestFocusInWindow()
        if (state == State.PLAYING) {
            birdV = JUMP
        } else {
            reset()
            state = State.PLAYING
        }
    }

    private fun die() {
        state = State.OVER
        if (score > highScore) highScore = score
    }

    override fun actionPerformed(e: ActionEvent) {
        if (state == State.PLAYING) {
            birdV += GRAVITY
            birdY += birdV
            frame++

            if (frame % 75 == 0) {
                val topH = 50 + (Math.random() * (VH - GAP - 100)).toInt()
                pipes.add(Pipe(VW.toDouble(), topH))
            }

            val bL = 50; val bR = 50 + BIRD_SIZE
            val bT = birdY.toInt(); val bB = bT + BIRD_SIZE

            val iter = pipes.iterator()
            while (iter.hasNext()) {
                val p = iter.next()
                p.x -= PIPE_SPEED
                val pL = p.x.toInt(); val pR = pL + PW
                if (bR > pL && bL < pR && (bT < p.topH || bB > p.topH + GAP)) die()
                if (!p.scored && p.x.toInt() + PW < 50) { score++; p.scored = true }
                if (p.x < -PW) iter.remove()
            }

            if (birdY < 0 || birdY > VH - GROUND_H - BIRD_SIZE) die()
        }
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val pw = width.toDouble()
        val ph = height.toDouble()
        val scale = minOf(pw / VW, ph / VH)
        val ox = (pw - VW * scale) / 2
        val oy = (ph - VH * scale) / 2

        val saved = g2.transform
        g2.translate(ox, oy)
        g2.scale(scale, scale)

        // Clip to virtual canvas
        g2.clip = Rectangle(0, 0, VW, VH)

        drawGame(g2)

        g2.transform = saved

        // Black bars
        g2.color = Color.BLACK
        if (ox > 0) { g2.fillRect(0, 0, ox.toInt(), height); g2.fillRect((ox + VW * scale).toInt(), 0, width, height) }
        if (oy > 0) { g2.fillRect(0, 0, width, oy.toInt()); g2.fillRect(0, (oy + VH * scale).toInt(), width, height) }
    }

    private fun drawGame(g2: Graphics2D) {
        // Sky
        g2.paint = GradientPaint(0f, 0f, Color(0x1a, 0x1a, 0x1a), 0f, VH.toFloat(), Color(0x1f, 0x1f, 0x1f))
        g2.fillRect(0, 0, VW, VH)

        // Clouds
        g2.color = Color(0x2a, 0x2a, 0x2a)
        val cs = frame * 0.3
        for (c in clouds) {
            var cx = ((c.x - cs) % 500.0); if (cx < -80) cx += 500.0
            g2.fill(Ellipse2D.Double(cx - c.s1, c.y - c.s1.toDouble(), c.s1 * 2.0, c.s1 * 2.0))
            g2.fill(Ellipse2D.Double(cx + 25 - c.s2, c.y - 5 - c.s2.toDouble(), c.s2 * 2.0, c.s2 * 2.0))
            g2.fill(Ellipse2D.Double(cx + 55 - c.s3, c.y - c.s3.toDouble(), c.s3 * 2.0, c.s3 * 2.0))
        }

        // Buildings
        val bs = frame * 0.5
        for (b in buildings) {
            var bx = ((b.x - bs) % 510.0); if (bx < -60) bx += 510.0
            g2.color = Color(0x1a, 0x1a, 0x1a)
            g2.fillRect(bx.toInt(), VH - b.h - GROUND_H, b.w, b.h)
            g2.color = Color(0x25, 0x25, 0x25)
            var wy = VH - b.h - GROUND_H + 5
            while (wy < VH - GROUND_H - 5) {
                var wx = bx.toInt() + 5
                while (wx < bx.toInt() + b.w - 5) {
                    if (wx in 0 until VW) g2.fillRect(wx, wy, 5, 8)
                    wx += 10
                }
                wy += 15
            }
        }

        // Ground
        g2.color = Color(0x15, 0x15, 0x15)
        g2.fillRect(0, VH - GROUND_H, VW, GROUND_H)
        g2.color = Color(0x2a, 0x2a, 0x2a)
        g2.fillRect(0, VH - GROUND_H, VW, 3)

        // Pipes
        for (p in pipes) {
            val px = p.x.toInt()
            g2.color = Color(0x7c, 0x3a, 0xed)
            g2.fillRect(px, 0, PW, p.topH)
            g2.color = Color(0x93, 0x33, 0xea)
            g2.fillRect(px - 3, p.topH - 15, PW + 6, 15)
            g2.color = Color(0x7c, 0x3a, 0xed)
            val botY = p.topH + GAP
            g2.fillRect(px, botY, PW, VH - botY)
            g2.color = Color(0x93, 0x33, 0xea)
            g2.fillRect(px - 3, botY, PW + 6, 15)
        }

        // Bird
        val bx = 50; val by = birdY.toInt()
        g2.paint = RadialGradientPaint(
            (bx + 10).toFloat(), (by + 10).toFloat(), 12f,
            floatArrayOf(0f, 1f), arrayOf(Color(0xa8, 0x55, 0xf7), Color(0x7c, 0x3a, 0xed))
        )
        g2.fill(Ellipse2D.Double((bx - 2).toDouble(), (by - 2).toDouble(), 24.0, 24.0))
        g2.color = Color.WHITE
        g2.fill(Ellipse2D.Double((bx + 11).toDouble(), (by + 5).toDouble(), 6.0, 6.0))
        g2.color = Color.BLACK
        g2.fill(Ellipse2D.Double((bx + 13).toDouble(), (by + 6.5), 3.0, 3.0))
        g2.color = Color(0x93, 0x33, 0xea)
        val wingY = by + 12 + (Math.sin(frame * 0.3) * 3).toInt()
        g2.fill(Ellipse2D.Double((bx - 1).toDouble(), (wingY - 4).toDouble(), 12.0, 8.0))

        // Score
        g2.color = Color.WHITE
        g2.font = Font("SansSerif", Font.BOLD, 24)
        drawCentered(g2, score.toString(), VW, 35)
        g2.color = Color(0x66, 0x66, 0x66)
        g2.font = Font("SansSerif", Font.PLAIN, 12)
        drawCentered(g2, "Best: $highScore", VW, 55)

        // Overlays
        if (state == State.IDLE || state == State.OVER) {
            g2.color = Color(0, 0, 0, 180)
            g2.fillRect(0, 0, VW, VH)
            if (state == State.IDLE) {
                g2.color = Color.WHITE
                g2.font = Font("SansSerif", Font.BOLD, 20)
                drawCentered(g2, "Driftbox Bird", VW, VH / 2 - 30)
                g2.color = Color(0x88, 0x88, 0x88)
                g2.font = Font("SansSerif", Font.PLAIN, 13)
                drawCentered(g2, "Click or press Space to start", VW, VH / 2 + 5)
                drawCentered(g2, "Space / ↑ to jump", VW, VH / 2 + 25)
            } else {
                g2.color = Color(0xef, 0x44, 0x44)
                g2.font = Font("SansSerif", Font.BOLD, 20)
                drawCentered(g2, "Game Over", VW, VH / 2 - 30)
                g2.color = Color.WHITE
                g2.font = Font("SansSerif", Font.BOLD, 28)
                drawCentered(g2, score.toString(), VW, VH / 2 + 10)
                g2.color = Color(0x88, 0x88, 0x88)
                g2.font = Font("SansSerif", Font.PLAIN, 12)
                drawCentered(g2, "Click to retry", VW, VH / 2 + 40)
            }
        }
    }

    private fun drawCentered(g2: Graphics2D, text: String, w: Int, y: Int) {
        val fm = g2.fontMetrics
        g2.drawString(text, (w - fm.stringWidth(text)) / 2, y)
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_SPACE || e.keyCode == KeyEvent.VK_UP) jump()
    }
    override fun keyReleased(e: KeyEvent) {}
    override fun keyTyped(e: KeyEvent) {}
    override fun mouseClicked(e: MouseEvent) { jump() }
    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}

    private enum class State { IDLE, PLAYING, OVER }
    private data class Pipe(var x: Double, val topH: Int, var scored: Boolean = false)
    private data class Cloud(val x: Double, val y: Double, val s1: Int, val s2: Int, val s3: Int)
    private data class Building(val x: Int, val w: Int, val h: Int)
}
