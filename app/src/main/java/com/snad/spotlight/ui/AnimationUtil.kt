package com.snad.spotlight.ui

import android.animation.Animator
import android.content.Context
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.animation.addListener
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.ScaleProvider
import kotlin.math.max


object AnimationUtil {

    fun startMaterialFade(context: Context, sceneRoot: ViewGroup, toVisible: Boolean = true, duration: Long = 200) {
        MaterialFade()
            //.setDuration(duration)
            //.addTransition(ScaleProvider(toVisible))
            //.start(sceneRoot)
    }

    fun circularRevealAnimation(view: View, onStartListener: (Animator) -> Unit) {
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
        createCircularReveal.addListener(onStart = onStartListener)
        createCircularReveal.start()
    }

    private fun TransitionSet.start(sceneRoot: ViewGroup) {
        TransitionManager.beginDelayedTransition(sceneRoot, this)
    }
}

