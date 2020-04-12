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

    private fun TransitionSet.start(sceneRoot: ViewGroup) {
        TransitionManager.beginDelayedTransition(sceneRoot, this)
    }
}

