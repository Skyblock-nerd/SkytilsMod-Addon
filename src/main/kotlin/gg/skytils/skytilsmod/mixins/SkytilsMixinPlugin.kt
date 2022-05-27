/*
 * Skytils - Hypixel Skyblock Quality of Life Mod
 * Copyright (C) 2022 Skytils
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.skytils.skytilsmod.mixins

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import net.minecraft.launchwrapper.Launch
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class SkytilsMixinPlugin : IMixinConfigPlugin {
    private val delegate: IMixinConfigPlugin =
        Class.forName("gg.skytils.skytilsmod.loader.SkytilsLoaderMixinPlugin").newInstance() as IMixinConfigPlugin

    val mixinPackage = "gg.skytils.skytilsmod.mixins.transformers"
    var deobfEnvironment = false

    override fun onLoad(mixinPackage: String) {
        delegate.onLoad(mixinPackage)
        deobfEnvironment = Launch.blackboard.getOrDefault("fml.deobfuscatedEnvironment", false) as Boolean
        if (deobfEnvironment) {
            println("We are in a deobfuscated environment, loading compatibility mixins.")
        }
        MixinExtrasBootstrap.init()
    }

    override fun getRefMapperConfig(): String? {
        return delegate.refMapperConfig
    }

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if (!mixinClassName.startsWith(mixinPackage)) {
            println("Woah, how did mixin $mixinClassName for $targetClassName get here?")
            return false
        }
        if (mixinClassName.startsWith("$mixinPackage.deobfenv") && !deobfEnvironment) {
            println("Mixin $mixinClassName is for a deobfuscated environment, disabling.")
            return false
        }
        return delegate.shouldApplyMixin(targetClassName, mixinClassName)
    }

    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: Set<String>) {
        delegate.acceptTargets(myTargets, otherTargets)
    }

    override fun getMixins(): List<String>? {
        return delegate.mixins
    }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        delegate.preApply(targetClassName, targetClass, mixinClassName, mixinInfo)
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo?
    ) {
        delegate.postApply(targetClassName, targetClass, mixinClassName, mixinInfo)
    }
}