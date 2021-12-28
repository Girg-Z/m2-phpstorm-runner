package com.github.girgz.m2phpstormrunner

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "M2RunnerSettings",
    storages = [Storage("m2runner.xml")]
)
class Settings : PersistentStateComponent<Settings.State> {

    data class State(
        var pluginEnabled: Boolean = false,
        var magentoVersion: String? = null
    )

    private var state: State = State()

    override fun getState(): State {
        return state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        @JvmStatic
        fun getInstance(): Settings {
            return ServiceManager.getService(Settings::class.java)
        }
    }

    fun getMagentoVersion(): String? {
        return this.state.magentoVersion
    }

    fun setMagentoVersion(version: String){
        this.state.magentoVersion = version
    }

}