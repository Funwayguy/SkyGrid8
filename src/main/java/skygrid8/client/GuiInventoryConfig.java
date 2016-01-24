package skygrid8.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import skygrid8.core.SkyGrid8;
import skygrid8.handlers.ConfigHandler;

public class GuiInventoryConfig extends GuiConfig
{
	public GuiInventoryConfig(GuiScreen parent)
	{
		super(parent, new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), SkyGrid8.MODID, false, false, SkyGrid8.NAME);
	}
}
