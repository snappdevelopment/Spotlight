package com.snad.spotlight.ui

import android.content.Context
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.Scale


object AnimationUtil {

    fun startMaterialFade(context: Context, sceneRoot: ViewGroup, toVisible: Boolean = true, duration: Long = 200) {
        MaterialFade
            .create(context)
            .setDuration(duration)
            .addTransition(Scale(toVisible))
            .start(sceneRoot)
    }

    fun startScaleTransition(target: View, scaleFactor: Float, duration: Long = 200) {
//        val animator = ObjectAnimator.ofFloat(target, View.SCALE_Y, scaleFactor)
//        animator.duration = duration
//        animator.start()
        val anim: Animation = ScaleAnimation(
            1f, 1f,  // Start and end values for the X axis scaling
            target.scaleY, scaleFactor,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0f
        ) // Pivot point of Y scaling

        anim.fillAfter = true // Needed to keep the result of the animation

        anim.duration = 1000
        target.startAnimation(anim)
    }

    private fun TransitionSet.start(sceneRoot: ViewGroup) {
        TransitionManager.beginDelayedTransition(sceneRoot, this)
    }
}

