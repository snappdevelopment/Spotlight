package com.snad.core.ui

import android.animation.Animator
import android.content.Context
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import com.google.android.material.transition.MaterialFade
import kotlin.math.max

object AnimationUtil {

    fun startMaterialFade(context: Context, sceneRoot: ViewGroup, toVisible: Boolean = true, duration: Long = 200) {
        MaterialFade().apply {
            this.duration = duration
        }
            //.setDuration(duration)
//            .addTransition(Scale(toVisible))
            //.start(sceneRoot)
    }

    fun circularRevealAnimation(view: View, onStartListener: () -> Unit) {
        val max: Int = max(view.width, view.height)
        val createCircularReveal = ViewAnimationUtils.createCircularReveal(
                view,
                view.width / 2,
                0,
                0F,
                max.toFloat()
            )
        createCircularReveal.duration = 600
        createCircularReveal.startDelay = 200
        createCircularReveal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                onStartListener()
            }
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        createCircularReveal.start()
    }

    private fun TransitionSet.start(sceneRoot: ViewGroup) {
        TransitionManager.beginDelayedTransition(sceneRoot, this)
    }
}

