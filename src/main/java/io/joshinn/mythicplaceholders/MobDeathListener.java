package io.joshinn.mythicplaceholders;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobDeathListener {

    private ConcurrentHashMap<UUID, HashMap<String, Integer>> stats = new ConcurrentHashMap<>();
    private HashMap<UUID, String> usernames = new HashMap<>();

    public MobDeathListener(){

        loadData();

        Events.subscribe(MythicMobDeathEvent.class)
                .filter(event -> event.getKiller() instanceof Player)
                .handler(event -> {
            String mythicMobID = event.getMobType().getInternalName();
            Player player = (Player) event.getKiller();

            if(!stats.containsKey(player.getUniqueId())){
                stats.put(player.getUniqueId(), new HashMap<>());
            }

            HashMap<String, Integer> statMap = stats.get(player.getUniqueId());

            statMap.put(mythicMobID, statMap.getOrDefault(mythicMobID, 0) + 1);

        });

        Events.subscribe(PlayerJoinEvent.class).handler(event -> {
           if(!usernames.containsKey(event.getPlayer().getUniqueId())){
               usernames.put(event.getPlayer().getUniqueId(), event.getPlayer().getName());
           } else {
               if(!usernames.get(event.getPlayer().getUniqueId()).equals(event.getPlayer().getName())){
                   usernames.put(event.getPlayer().getUniqueId(), event.getPlayer().getName());
               }
           }
        });
    }

    private void loadData(){

        stats.clear();

        for(String uuidString : Property.NodeList(Scope.DATA, "").get()){
            UUID uuid = UUID.fromString(uuidString);
            String username = Property.String(Scope.DATA, uuidString + ".Username").get();
            HashMap<String, Integer> statMap = new HashMap<>();
            for(String statString : Property.StringList(Scope.DATA, uuidString + ".Stats").get()){
                String[] stat = statString.split("\\|");
                statMap.put(stat[0], Integer.parseInt(stat[1]));
            }
            stats.put(uuid, statMap);
            usernames.put(uuid, username);
        }
    }

    public void saveData(){
        for(UUID uuid : stats.keySet()){
            HashMap<String, Integer> statMap = stats.get(uuid);

            List<String> statsList = new ArrayList<>();
            for(String stat : statMap.keySet()){
                statsList.add(stat + "|" + statMap.get(stat));
            }

            Property.StringList(Scope.DATA, uuid.toString() + ".Stats").set(statsList);
            Property.String(Scope.DATA, uuid + ".Username").set(usernames.get(uuid));
        }
    }

    public int getMobKills(UUID player){
        return getMobKills(player, null);
    }

    public int getMobKills(UUID player, String mob){
        if(!stats.containsKey(player)){
            return 0;
        }

        if(mob == null){
            int total = 0;
            for(String mythicMobID : stats.get(player).keySet()){
                total += stats.get(player).get(mythicMobID);
            }
            return total;
        }else{
            if(!stats.get(player).containsKey(mob)){
                return 0;
            }
            return stats.get(player).get(mob);
        }
    }

    public String getPlayerInPosition(int position){
        //Sort the stats map by total kills and return the UUID in that position.
        String uuid = stats.keySet().stream()
                .sorted((o1, o2) -> Integer.compare(getMobKills(o2), getMobKills(o1)))
                .skip(position - 1)
                .findFirst()
                .map(UUID::toString)
                .orElse("No one in this position.");

        return usernames.get(UUID.fromString(uuid));
    }
}
