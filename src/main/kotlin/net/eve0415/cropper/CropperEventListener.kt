package net.eve0415.cropper

import kotlinx.coroutines.delay
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.type.Leaves
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.player.PlayerInteractEvent

class CropperEventListener(private val plugin: Cropper) : Listener {
    private val blacklist = listOf(Material.PUMPKIN_STEM, Material.MELON_STEM)
    private val relative =
        listOf(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: PlayerInteractEvent) {
        if (!event.action.isRightClick) return
        if (event.player.isSneaking) return

        val world = event.clickedBlock?.world ?: return
        val block = event.clickedBlock ?: return
        val blockData = block.blockData

        if (blockData !is Ageable) return
        if (blockData.age != blockData.maximumAge) return

        if (event.isBlockInHand) event.setUseItemInHand(Event.Result.DENY)
        if (blacklist.contains(block.type)) return

        if (!event.player.breakBlock(block)) return

        blockData.age = 0
        world.setBlockData(block.location, blockData)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun onEvent(event: BlockBreakEvent) {
        decayLeaves(event.block)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun onEvent(event: LeavesDecayEvent) {
        decayLeaves(event.block)
    }

    private suspend fun decayLeaves(block: Block) {
        val world = block.world
        val blockData = block.blockData

        if (!(Tag.LEAVES.isTagged(block.type) || isLog(block) && !blockData.asString.contains("stripped"))) return

        if (blockData is Leaves) {
            if (blockData.isPersistent) return
            if (blockData.distance < 7) {
                for (i in (blockData.distance * -1)..blockData.distance) {
                    for (j in (blockData.distance * -1)..blockData.distance) {
                        for (k in (blockData.distance * -1)..blockData.distance) {
                            if (isLog(world.getBlockAt(block.location.add(i.toDouble(), j.toDouble(), k.toDouble())))) {
                                return
                            }
                        }
                    }
                }
            }
        } else if (relative.map { block.getRelative(it).blockData.material.name }.find { it.contains("LOG") } != null) {
            return
        }

        relative.shuffled().forEach {
            val relative = block.getRelative(it)
            val relativeData = relative.blockData

            if (relativeData !is Leaves) return@forEach
            if (relativeData.isPersistent) return@forEach

            delay(50)

            plugin.server.pluginManager.callEvent(LeavesDecayEvent(relative))
            relative.breakNaturally(true)
        }
    }

    private fun isLog(block: Block): Boolean {
        return block.blockData.material.name.contains("LOG")
    }
}
