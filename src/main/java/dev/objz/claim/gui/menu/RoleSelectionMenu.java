package dev.objz.claim.gui.menu;

import dev.objz.claim.Claim;
import dev.objz.claim.model.ClaimRegion;
import dev.objz.claim.model.ClaimRole;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class RoleSelectionMenu extends ClaimGuiHolder {
	private final Claim plugin;

	public RoleSelectionMenu(Claim plugin, ClaimRegion claim) {
		super(claim, 27, Component.text("Select Role to Edit"));
		this.plugin = plugin;
		int slot = 11;
		for (ClaimRole role : ClaimRole.values()) {
			if (role == ClaimRole.OWNER)
				continue;

			setItem(slot++, Material.PAPER, role.getDisplayName(), List.of("Edit permissions"));
		}
		setItem(22, Material.ARROW, "Back", null);
	}

	@Override
	public void handleClick(Player player, int slot) {
		if (slot == 22) {
			player.openInventory(new MainMenu(plugin, claim, player).getInventory());
			return;
		}
		List<ClaimRole> roles = Arrays.stream(ClaimRole.values()).filter(r -> r != ClaimRole.OWNER).toList();
		int index = slot - 11;
		if (index >= 0 && index < roles.size()) {
			player.openInventory(new SettingsMenu(plugin, claim, false, roles.get(index)).getInventory());
		}
	}
}
