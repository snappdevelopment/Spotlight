package com.snad.core.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

abstract class StateMachine<STATE, ACTION>(
    initialState: STATE
): ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected suspend fun updateState(state: STATE) {
        _state.emit(state)
    }

    abstract fun handleAction(action: ACTION)
}