/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.files;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import tk.wurst_client.altmanager.Alt;
import tk.wurst_client.altmanager.Encryption;
import tk.wurst_client.altmanager.screens.GuiAltList;
import tk.wurst_client.utils.JsonUtils;

public final class AltsConfig extends Config
{
	private final Encryption encryption = new Encryption();
	
	public AltsConfig()
	{
		super("alts.json");
	}
	
	@Override
	protected JsonElement readFile(Path path) throws IOException
	{
		return JsonUtils.jsonParser.parse(encryption.loadEncryptedFile(path));
	}
	
	@Override
	protected void writeFile(Path path, JsonElement json) throws IOException
	{
		encryption.saveEncryptedFile(path, JsonUtils.prettyGson.toJson(json));
	}
	
	@Override
	protected void loadFromJson(JsonElement json)
	{
		GuiAltList.alts.clear();
		
		for(Entry<String, JsonElement> entry : json.getAsJsonObject()
			.entrySet())
		{
			JsonObject jsonAlt = entry.getValue().getAsJsonObject();
			
			String email = entry.getKey();
			String password = jsonAlt.get("password") == null ? ""
				: jsonAlt.get("password").getAsString();
			String name = jsonAlt.get("name") == null ? ""
				: jsonAlt.get("name").getAsString();
			boolean starred = jsonAlt.get("starred") == null ? false
				: jsonAlt.get("starred").getAsBoolean();
			
			GuiAltList.alts.add(new Alt(email, password, name, starred));
		}
		
		GuiAltList.sortAlts();
	}
	
	@Override
	protected JsonElement saveToJson()
	{
		JsonObject json = new JsonObject();
		
		for(Alt alt : GuiAltList.alts)
		{
			JsonObject jsonAlt = new JsonObject();
			
			jsonAlt.addProperty("password", alt.getPassword());
			jsonAlt.addProperty("name", alt.getName());
			jsonAlt.addProperty("starred", alt.isStarred());
			
			json.add(alt.getEmail(), jsonAlt);
		}
		
		return json;
	}
}
