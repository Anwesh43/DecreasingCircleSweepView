package com.anwesh.uiprojects.dcsview

/**
 * Created by anweshmishra on 14/07/18.
 */

import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF


val node_colors : List<Int> = arrayOf("#f44336", "#3F51B5", "#9C27B0", "4CAF50", "#283593").map {color -> Color.parseColor(color)}

class DCSView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent): Boolean {
        when (event.action == MotionEvent.ACTION_DOWN) {

        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {
        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }
    }

    data class DCSNode(var i : Int= 0, val state : State =  State()) {

        private var next : DCSNode? = null

        private var prev : DCSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < node_colors.size - 1) {
                next = DCSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val r = Math.min(w, h) / (node_colors.size * 2)
            val currR : Float = r * (i + 1)
            paint.color = node_colors[i]
            canvas.save()
            canvas.translate(w/2, h/2)
            canvas.drawArc(RectF(-currR, -currR, currR, currR), 360f * state.scale, 360 - 360f * state.scale, true, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DCSNode{
            var curr : DCSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class DCS(var i : Int) {

        private var curr : DCSNode = DCSNode()

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Int) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }
}