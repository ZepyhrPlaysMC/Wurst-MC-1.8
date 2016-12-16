/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.navigator.NavigatorItem;

@Mod.Info(
	description = "Switches the item in your hand all the time.\n"
		+ "Tip: Use this in combination with BuildRandom while\n"
		+ "having a lot of different colored wool blocks in your\n" + "hotbar.",
	name = "AutoSwitch",
	tags = "auto switch",
	help = "Mods/AutoSwitch")
public class AutoSwitchMod extends Mod implements UpdateListener
{
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.buildRandomMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.player.inventory.currentItem == 8)
			mc.player.inventory.currentItem = 0;
		else
			mc.player.inventory.currentItem++;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
