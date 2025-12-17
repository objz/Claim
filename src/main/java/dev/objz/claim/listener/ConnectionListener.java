package dev.objz.claim.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.objz.claim.Claim;
import dev.objz.claim.util.HeadUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener extends AbstractListener {

	public ConnectionListener(Claim plugin) {
		super(plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PlayerProfile profile = event.getPlayer().getPlayerProfile();
		
		if (!profile.hasProperty("textures")) {
			profile.complete(false); 
		}

		for (ProfileProperty property : profile.getProperties()) {
			if ("textures".equals(property.getName())) {
				HeadUtil.cacheTexture(event.getPlayer().getUniqueId(), property.getValue());
				break;
			}
		}
	}
}
