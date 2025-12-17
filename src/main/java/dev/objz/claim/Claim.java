package dev.objz.claim;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.objz.claim.command.ClaimCommands;
import dev.objz.claim.gui.GuiManager;
import dev.objz.claim.integration.ClaimBlueMap;
import dev.objz.claim.listener.ClaimListener;
import dev.objz.claim.manager.ClaimManager;
import dev.objz.claim.manager.SelectionManager;
import dev.objz.claim.util.HeadUtil;
import dev.objz.claim.visual.Border;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Claim extends JavaPlugin {

	private static Claim instance;
	private ClaimManager claimManager;
	private SelectionManager selectionManager;
	private Border borderVisualizer;
	private GuiManager guiManager;
	private ClaimBlueMap blueMapIntegration;

	@Override
	public void onLoad() {
		CommandAPI.onLoad(new CommandAPIPaperConfig(this).verboseOutput(false));
	}

	@Override
	public void onEnable() {
		instance = this;
		CommandAPI.onEnable();

		HeadUtil.loadCache(new File(getDataFolder(), "head_cache.yml"));

		this.borderVisualizer = new Border(this);
		this.claimManager = new ClaimManager(this);
		this.selectionManager = new SelectionManager(this);
		this.guiManager = new GuiManager(this);

		new ClaimCommands(this).register();

		getServer().getPluginManager().registerEvents(new ClaimListener(this), this);
		getServer().getPluginManager().registerEvents(selectionManager, this);
		getServer().getPluginManager().registerEvents(guiManager, this);

		// Integrations
		if (getServer().getPluginManager().isPluginEnabled("BlueMap")) {
			this.blueMapIntegration = new ClaimBlueMap(this);
			this.blueMapIntegration.enable();
		}

		getLogger().info("Claim plugin enabled for Folia!");
	}

	@Override
	public void onDisable() {
		CommandAPI.onDisable();
		if (blueMapIntegration != null) {
			blueMapIntegration.disable();
		}
		if (borderVisualizer != null) {
			borderVisualizer.cleanup();
		}
		if (claimManager != null) {
			claimManager.saveClaims();
		}
		HeadUtil.saveCache(new File(getDataFolder(), "head_cache.yml"));
	}

	public static Claim getInstance() {
		return instance;
	}

	public ClaimManager getClaimManager() {
		return claimManager;
	}

	public SelectionManager getSelectionManager() {
		return selectionManager;
	}

	public Border getBorderVisualizer() {
		return borderVisualizer;
	}

	public GuiManager getGuiManager() {
		return guiManager;
	}
}
