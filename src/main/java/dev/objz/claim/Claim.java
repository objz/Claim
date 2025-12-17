package dev.objz.claim;

import dev.objz.claim.command.ClaimCommands;
import dev.objz.claim.gui.GuiManager;
import dev.objz.claim.integration.ClaimBlueMap;
import dev.objz.claim.listener.*;
import dev.objz.claim.manager.BypassManager;
import dev.objz.claim.manager.ClaimManager;
import dev.objz.claim.manager.SelectionManager;
import dev.objz.claim.util.HeadUtil;
import dev.objz.claim.visual.Border;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Claim extends JavaPlugin {

	private static Claim instance;
	private ClaimManager claimManager;
	private SelectionManager selectionManager;
	private BypassManager bypassManager;
	private Border borderVisualizer;
	private GuiManager guiManager;
	private ClaimBlueMap blueMapIntegration;

	@Override
	public void onEnable() {
		instance = this;

		HeadUtil.loadCache(new File(getDataFolder(), "head_cache.yml"));

		this.borderVisualizer = new Border(this);
		this.claimManager = new ClaimManager(this);
		this.selectionManager = new SelectionManager(this);
		this.bypassManager = new BypassManager();
		this.guiManager = new GuiManager(this);

		new ClaimCommands(this).register();

		registerListeners();

		// Integrations
		if (getServer().getPluginManager().isPluginEnabled("BlueMap")) {
			getServer().getLogger().info("Enabling BlueMap integration");
			this.blueMapIntegration = new ClaimBlueMap(this);
			this.blueMapIntegration.enable();
		}
	}

	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ConnectionListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new InteractListener(this), this);
		pm.registerEvents(new ItemListener(this), this);
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new MovementListener(this), this);

		pm.registerEvents(selectionManager, this);
		pm.registerEvents(guiManager, this);
	}

	@Override
	public void onDisable() {
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

	public BypassManager getBypassManager() {
		return bypassManager;
	}

	public Border getBorderVisualizer() {
		return borderVisualizer;
	}

	public GuiManager getGuiManager() {
		return guiManager;
	}
}
