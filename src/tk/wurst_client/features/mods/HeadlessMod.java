/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import tk.wurst_client.events.listeners.UpdateListener;

@Mod.Info(
	description = "While this is active, other people will think you are\n"
		+ "headless. Looks hilarious!",
	name = "Headless",
	tags = "head less",
	help = "Mods/Headless")
@Mod.Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public final class HeadlessMod extends Mod implements UpdateListener
{
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation(
			Minecraft.getMinecraft().player.rotationYaw, 180F,
			Minecraft.getMinecraft().player.onGround));
	}
}
