/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import tk.wurst_client.events.PacketInputEvent;
import tk.wurst_client.events.listeners.PacketInputListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.utils.ChatUtils;
import tk.wurst_client.utils.ClickType;
import tk.wurst_client.utils.InventoryUtils;
import tk.wurst_client.utils.SoundEvents;

@Mod.Info(
	description = "Automatically catches fish until either all of your fishing rods are completely used up or your\n"
		+ "inventory is completely full. If fishing rods are placed outside of the hotbar, they will\n"
		+ "automatically be moved into the hotbar once needed.",
	name = "AutoFish",
	tags = "FishBot, auto fish, fish bot, fishing",
	help = "Mods/AutoFish")
@Mod.Bypasses
public final class AutoFishMod extends Mod
	implements UpdateListener, PacketInputListener
{
	private int timer;
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(PacketInputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(PacketInputListener.class, this);
		
		// reset timer
		timer = 0;
	}
	
	@Override
	public void onUpdate()
	{
		// check if inventory is full
		if(mc.player.inventory.getFirstEmptyStack() == -1)
		{
			ChatUtils.message("Inventory is full.");
			setEnabled(false);
			return;
		}
		
		// search fishing rod in hotbar
		int rodInHotbar = -1;
		for(int i = 0; i < 9; i++)
		{
			// skip non-rod items
			ItemStack stack = mc.player.inventory.getStackInSlot(i);
			if(InventoryUtils.isEmptySlot(stack)
				|| !(stack.getItem() instanceof ItemFishingRod))
				continue;
			
			rodInHotbar = i;
			break;
		}
		
		// check if any rod was found
		if(rodInHotbar != -1)
		{
			// select fishing rod
			if(mc.player.inventory.currentItem != rodInHotbar)
			{
				mc.player.inventory.currentItem = rodInHotbar;
				return;
			}
			
			// wait for timer
			if(timer > 0)
			{
				timer--;
				return;
			}
			
			// check bobber
			if(mc.player.fishEntity != null)
				return;
			
			// cast rod
			rightClick();
			return;
		}
		
		// search fishing rod in inventory
		int rodInInventory = -1;
		for(int i = 9; i < 36; i++)
		{
			// skip non-rod items
			ItemStack stack = mc.player.inventory.getStackInSlot(i);
			if(InventoryUtils.isEmptySlot(stack)
				|| !(stack.getItem() instanceof ItemFishingRod))
				continue;
			
			rodInInventory = i;
			break;
		}
		
		// check if completely out of rods
		if(rodInInventory == -1)
		{
			ChatUtils.message("Out of fishing rods.");
			setEnabled(false);
			return;
		}
		
		// find empty hotbar slot
		int hotbarSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			// skip non-empty slots
			if(!InventoryUtils.isSlotEmpty(i))
				continue;
			
			hotbarSlot = i;
			break;
		}
		
		// check if hotbar is full
		boolean swap = false;
		if(hotbarSlot == -1)
		{
			hotbarSlot = mc.player.inventory.currentItem;
			swap = true;
		}
		
		// place rod in hotbar slot
		mc.playerController.windowClick(0, rodInInventory, 0, ClickType.PICKUP,
			mc.player);
		mc.playerController.windowClick(0, 36 + hotbarSlot, 0, ClickType.PICKUP,
			mc.player);
		
		// swap old hotbar item with rod
		if(swap)
			mc.playerController.windowClick(0, rodInInventory, 0,
				ClickType.PICKUP, mc.player);
	}
	
	@Override
	public void onReceivedPacket(PacketInputEvent event)
	{
		// check packet type
		if(!(event.getPacket() instanceof SPacketSoundEffect))
			return;
		
		// check sound type
		if(!SoundEvents.ENTITY_BOBBER_SPLASH
			.equals(((SPacketSoundEffect)event.getPacket()).getSound()))
			return;
		
		// catch fish
		rightClick();
	}
	
	private void rightClick()
	{
		// check held item
		ItemStack stack = mc.player.inventory.getCurrentItem();
		if(InventoryUtils.isEmptySlot(stack)
			|| !(stack.getItem() instanceof ItemFishingRod))
			return;
		
		// right click
		mc.rightClickMouse();
		
		// reset timer
		timer = 15;
	}
}
