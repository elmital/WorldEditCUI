package com.mumfrey.worldeditcui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mumfrey.worldeditcui.InitialisationFactory;
import com.mumfrey.worldeditcui.render.ConfiguredColour;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores and reads WorldEditCUI settings
 *
 * @author yetanotherx
 * @author Adam Mummery-Smith
 * @author Jesús Sanz - Modified to work with the config GUI implementation
 */
public final class CUIConfiguration implements InitialisationFactory
{
	private static final String CONFIG_FILE_NAME = "worldeditcui.config.json";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private boolean debugMode = false;
	private boolean ignoreUpdates = false;
	private boolean promiscuous = false;
	private boolean alwaysOnTop = false;
	private boolean clearAllOnKey = false;

    private Colour cuboidGridColor = ConfiguredColour.CUBOIDBOX.getDefault();
    private Colour cuboidEdgeColor = ConfiguredColour.CUBOIDGRID.getDefault();
    private Colour cuboidFirstPointColor = ConfiguredColour.CUBOIDPOINT1.getDefault();
    private Colour cuboidSecondPointColor = ConfiguredColour.CUBOIDPOINT2.getDefault();
    private Colour polyGridColor = ConfiguredColour.POLYGRID.getDefault();
    private Colour polyEdgeColor = ConfiguredColour.POLYBOX.getDefault();
    private Colour polyPointColor = ConfiguredColour.POLYPOINT.getDefault();
    private Colour ellipsoidGridColor = ConfiguredColour.ELLIPSOIDGRID.getDefault();
    private Colour ellipsoidPointColor = ConfiguredColour.ELLIPSOIDCENTRE.getDefault();
    private Colour cylinderGridColor = ConfiguredColour.CYLINDERGRID.getDefault();
    private Colour cylinderEdgeColor = ConfiguredColour.CYLINDERBOX.getDefault();
    private Colour cylinderPointColor = ConfiguredColour.CYLINDERCENTRE.getDefault();
    private Colour chunkBoundaryColour = ConfiguredColour.CHUNKBOUNDARY.getDefault();
    private Colour chunkGridColour = ConfiguredColour.CHUNKGRID.getDefault();

	private static transient Map<String, Object> configArray = new LinkedHashMap<>();

	/**
	 * Copies the default config file to the proper directory if it does not
	 * exist. It then reads the file and sets each variable to the proper value.
	 */
	@Override
	public void initialise()
	{
		int index = 0;
		try
		{
			for (Field field : this.getClass().getDeclaredFields())
			{
				if (field.getType() == Colour.class)
				{
					ConfiguredColour configuredColour = ConfiguredColour.values()[index++];
					Colour colour = Colour.firstOrDefault((Colour)field.get(this), configuredColour.getColour().getHex());
					field.set(this, colour);
					configuredColour.setColour(colour);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		this.save();
	}

	public boolean isDebugMode()
	{
		return this.debugMode;
	}

	public boolean ignoreUpdates()
	{
		return this.ignoreUpdates;
	}

	public boolean isPromiscuous()
	{
		return this.promiscuous;
	}

	public void setPromiscuous(boolean promiscuous)
	{
		this.promiscuous = promiscuous;
	}

	public boolean isAlwaysOnTop()
	{
		return this.alwaysOnTop;
	}

	public void setAlwaysOnTop(boolean alwaysOnTop)
	{
		this.alwaysOnTop = alwaysOnTop;
	}

	public boolean isClearAllOnKey()
	{
		return this.clearAllOnKey;
	}

	public void setClearAllOnKey(boolean clearAllOnKey)
	{
		this.clearAllOnKey = clearAllOnKey;
	}

	private static Path getConfigFile()
	{
		return FabricLoader.getInstance().getConfigDir().resolve(CUIConfiguration.CONFIG_FILE_NAME);
	}

	public static CUIConfiguration create()
	{
		Path jsonFile = getConfigFile();

		CUIConfiguration config = null;
		if (Files.exists(jsonFile))
		{
			try (Reader fileReader = Files.newBufferedReader(jsonFile, StandardCharsets.UTF_8))
			{
				config = CUIConfiguration.GSON.fromJson(fileReader, CUIConfiguration.class);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		if (config == null) // load failed or file didn't exist
		{
			config = new CUIConfiguration();
		}


		configArray.put("debugMode", config.debugMode);
		configArray.put("ignoreUpdates", config.ignoreUpdates);
		configArray.put("promiscuous", config.promiscuous);
		configArray.put("alwaysOnTop", config.alwaysOnTop);
		configArray.put("clearAllOnKey", config.clearAllOnKey);

		configArray.put("cuboidGridColor", config.cuboidGridColor);
		configArray.put("cuboidEdgeColor", config.cuboidEdgeColor);
		configArray.put("cuboidFirstPointColor", config.cuboidFirstPointColor);
		configArray.put("cuboidSecondPointColor", config.cuboidSecondPointColor);
		configArray.put("polyGridColor", config.polyGridColor);
		configArray.put("polyEdgeColor", config.polyEdgeColor);
		configArray.put("polyPointColor", config.polyPointColor);
		configArray.put("ellipsoidGridColor", config.ellipsoidGridColor);
		configArray.put("ellipsoidPointColor", config.ellipsoidPointColor);
		configArray.put("cylinderGridColor", config.cylinderGridColor);
		configArray.put("cylinderEdgeColor", config.cylinderEdgeColor);
		configArray.put("cylinderPointColor", config.cylinderPointColor);
		configArray.put("chunkBoundaryColour", config.chunkBoundaryColour);
		configArray.put("chunkGridColour", config.chunkGridColour);

		return config;
	}

	public void changeValue(String text, Object value) {
		configArray.replace(text, value);
	}

	public Map<String, Object> getConfigArray() {
		return configArray;
	}

	public void configChanged() {
		debugMode 				= (Boolean) configArray.get("debugMode");
		ignoreUpdates 			= (Boolean) configArray.get("ignoreUpdates");
		promiscuous 			= (Boolean) configArray.get("promiscuous");
		alwaysOnTop 			= (Boolean) configArray.get("alwaysOnTop");
		clearAllOnKey 			= (Boolean) configArray.get("clearAllOnKey");

		cuboidGridColor 		= (Colour) 	configArray.get("cuboidGridColor");
		cuboidEdgeColor 		= (Colour) 	configArray.get("cuboidEdgeColor");
		cuboidFirstPointColor 	= (Colour) 	configArray.get("cuboidFirstPointColor");
		cuboidSecondPointColor 	= (Colour) 	configArray.get("cuboidSecondPointColor");
		polyGridColor 			= (Colour) 	configArray.get("polyGridColor");
		polyEdgeColor 			= (Colour) 	configArray.get("polyEdgeColor");
		polyPointColor 			= (Colour) 	configArray.get("polyPointColor");
		ellipsoidGridColor 		= (Colour) 	configArray.get("ellipsoidGridColor");
		ellipsoidPointColor 	= (Colour) 	configArray.get("ellipsoidPointColor");
		cylinderGridColor 		= (Colour) 	configArray.get("cylinderGridColor");
		cylinderEdgeColor 		= (Colour) 	configArray.get("cylinderEdgeColor");
		cylinderPointColor 		= (Colour) 	configArray.get("cylinderPointColor");
		chunkBoundaryColour 	= (Colour) 	configArray.get("chunkBoundaryColour");
		chunkGridColour 		= (Colour) 	configArray.get("chunkGridColour");
		this.initialise();
	}

	public Object getDefaultValue(String text) {
		switch(text) {
			case "debugMode":
			case "ignoreUpdates":
			case "promiscuous":
			case "alwaysOnTop":
			case "clearAllOnKey": return false;

			case "cuboidGridColor": return ConfiguredColour.CUBOIDBOX.getDefault();
			case "cuboidEdgeColor": return ConfiguredColour.CUBOIDGRID.getDefault();
			case "cuboidFirstPointColor": return ConfiguredColour.CUBOIDPOINT1.getDefault();
			case "cuboidSecondPointColor": return ConfiguredColour.CUBOIDPOINT2.getDefault();
			case "polyGridColor": return ConfiguredColour.POLYGRID.getDefault();
			case "polyEdgeColor": return ConfiguredColour.POLYBOX.getDefault();
			case "polyPointColor": return ConfiguredColour.POLYPOINT.getDefault();
			case "ellipsoidGridColor": return ConfiguredColour.ELLIPSOIDGRID.getDefault();
			case "ellipsoidPointColor": return ConfiguredColour.ELLIPSOIDCENTRE.getDefault();
			case "cylinderGridColor": return ConfiguredColour.CYLINDERGRID.getDefault();
			case "cylinderEdgeColor": return ConfiguredColour.CYLINDERBOX.getDefault();
			case "cylinderPointColor": return ConfiguredColour.CYLINDERCENTRE.getDefault();
			case "chunkBoundaryColour": return ConfiguredColour.CHUNKBOUNDARY.getDefault();
			case "chunkGridColour": return ConfiguredColour.CHUNKGRID.getDefault();
		}

		return null;
	}

	public String getDefaultDisplayName(String text) {
		switch(text) {
			case "debugMode": return I18n.translate("worldeditcui.options.debugMode");
			case "ignoreUpdates": return I18n.translate("worldeditcui.options.ignoreUpdates");
			case "promiscuous": return I18n.translate("worldeditcui.options.compat.spammy");
			case "alwaysOnTop": return I18n.translate("worldeditcui.options.compat.ontop");
			case "clearAllOnKey": return I18n.translate("worldeditcui.options.extra.clearall");

			case "cuboidGridColor": return I18n.translate("worldeditcui.color.cuboidgrid");
			case "cuboidEdgeColor": return I18n.translate("worldeditcui.color.cuboidedge");
			case "cuboidFirstPointColor": return I18n.translate("worldeditcui.color.cuboidpoint1");
			case "cuboidSecondPointColor": return I18n.translate("worldeditcui.color.cuboidpoint2");
			case "polyGridColor": return I18n.translate("worldeditcui.color.polygrid");
			case "polyEdgeColor": return I18n.translate("worldeditcui.color.polyedge");
			case "polyPointColor": return I18n.translate("worldeditcui.color.polypoint");
			case "ellipsoidGridColor": return I18n.translate("worldeditcui.color.ellipsoidgrid");
			case "ellipsoidPointColor": return I18n.translate("worldeditcui.color.ellipsoidpoint");
			case "cylinderGridColor": return I18n.translate("worldeditcui.color.cylindergrid");
			case "cylinderEdgeColor": return I18n.translate("worldeditcui.color.cylinderedge");
			case "cylinderPointColor": return I18n.translate("worldeditcui.color.cylinderpoint");
			case "chunkBoundaryColour": return I18n.translate("worldeditcui.color.chunkboundary");
			case "chunkGridColour": return I18n.translate("worldeditcui.color.chunkgrid");
		}

		return null;
	}

	public void save()
	{
		try(Writer fileWriter = Files.newBufferedWriter(getConfigFile(), StandardCharsets.UTF_8))
		{
			CUIConfiguration.GSON.toJson(this, fileWriter);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
