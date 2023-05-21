package io.joshinn.mythicplaceholders;

import io.lumine.mythic.bukkit.utils.Schedulers;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderManager extends PlaceholderExpansion {

    private MythicPlaceholders plugin;

    public PlaceholderManager(MythicPlaceholders plugin){
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(identifier.equalsIgnoreCase("mmkills")){
            return String.valueOf(plugin.getMobDeathListener().getMobKills(player.getUniqueId()));
        }

        if(identifier.startsWith("mmkills_")){
            String mobName = identifier.substring(8);
            return String.valueOf(plugin.getMobDeathListener().getMobKills(player.getUniqueId(), mobName));
        }

        if(identifier.startsWith("leaderboard_")){
            int position = Integer.parseInt(identifier.substring(12));
            try {
                return plugin.getMobDeathListener().getPlayerInPosition(position);
            } catch (Exception e) {
                return "No Player";
            }
        }

        return "Placeholder Error";
    }

    @Override
    public String getIdentifier() {
        return "mythicplaceholders";
    }

    @Override
    public String getAuthor() {
        return "Joshinn";
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }
}
