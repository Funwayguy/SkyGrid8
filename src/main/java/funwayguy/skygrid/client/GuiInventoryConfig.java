package funwayguy.skygrid.client;

import funwayguy.skygrid.core.SkyGrid;
import funwayguy.skygrid.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiInventoryConfig extends GuiConfig
{
	public GuiInventoryConfig(GuiScreen parent)
	{
		super(parent, new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), SkyGrid.MODID, false, false, SkyGrid.NAME);
	}
}
