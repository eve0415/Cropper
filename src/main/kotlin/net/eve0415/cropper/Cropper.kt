package net.eve0415.cropper

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents

@Suppress("unused")
class Cropper : SuspendingJavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerSuspendingEvents(CropperEventListener(this), this)
        logger.info("Cropper has been enabled!")
    }

    override fun onDisable() {
        logger.info("Cropper has been disabled!")
    }
}
