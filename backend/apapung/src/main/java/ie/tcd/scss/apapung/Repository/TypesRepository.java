package ie.tcd.scss.apapung.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class TypesRepository {
    private final Map<String, String> typesUrlMap;
    // Initialise the map of types
    public TypesRepository () {
        typesUrlMap = Map.ofEntries(Map.entry("normal", "https://archives.bulbagarden.net/media/upload/thumb/8/89/NormalIC_BDSP.png/105px-NormalIC_BDSP.png"),
                Map.entry("fighting", "https://archives.bulbagarden.net/media/upload/thumb/3/35/FightingIC_BDSP.png/105px-FightingIC_BDSP.png"),
                Map.entry("flying", "https://archives.bulbagarden.net/media/upload/thumb/0/03/FlyingIC_BDSP.png/105px-FlyingIC_BDSP.png"),
                Map.entry("poison", "https://archives.bulbagarden.net/media/upload/thumb/8/8b/PoisonIC_BDSP.png/105px-PoisonIC_BDSP.png"),
                Map.entry("ground", "https://archives.bulbagarden.net/media/upload/thumb/b/bd/GroundIC_BDSP.png/105px-GroundIC_BDSP.png"),
                Map.entry("rock", "https://archives.bulbagarden.net/media/upload/thumb/9/98/RockIC_BDSP.png/105px-RockIC_BDSP.png"),
                Map.entry("bug", "https://archives.bulbagarden.net/media/upload/thumb/9/9c/BugIC_BDSP.png/105px-BugIC_BDSP.png"),
                Map.entry("ghost", "https://archives.bulbagarden.net/media/upload/thumb/4/45/GhostIC_BDSP.png/105px-GhostIC_BDSP.png"),
                Map.entry("steel", "https://archives.bulbagarden.net/media/upload/thumb/5/5f/SteelIC_BDSP.png/105px-SteelIC_BDSP.png"),
                Map.entry("fire", "https://archives.bulbagarden.net/media/upload/thumb/b/b1/FireIC_BDSP.png/105px-FireIC_BDSP.png"),
                Map.entry("water", "https://archives.bulbagarden.net/media/upload/thumb/2/2b/WaterIC_BDSP.png/105px-WaterIC_BDSP.png"),
                Map.entry("grass", "https://archives.bulbagarden.net/media/upload/thumb/a/ad/GrassIC_BDSP.png/105px-GrassIC_BDSP.png"),
                Map.entry("electric", "https://archives.bulbagarden.net/media/upload/thumb/c/c1/ElectricIC_BDSP.png/105px-ElectricIC_BDSP.png"),
                Map.entry("psychic", "https://archives.bulbagarden.net/media/upload/thumb/6/68/PsychicIC_BDSP.png/105px-PsychicIC_BDSP.png"),
                Map.entry("ice", "https://archives.bulbagarden.net/media/upload/thumb/1/11/IceIC_BDSP.png/105px-IceIC_BDSP.png"),
                Map.entry("dragon", "https://archives.bulbagarden.net/media/upload/thumb/c/c7/DragonIC_BDSP.png/105px-DragonIC_BDSP.png"),
                Map.entry("dark", "https://archives.bulbagarden.net/media/upload/thumb/b/b9/DarkIC_BDSP.png/105px-DarkIC_BDSP.png"),
                Map.entry("fairy", "https://archives.bulbagarden.net/media/upload/thumb/d/d6/FairyIC_BDSP.png/105px-FairyIC_BDSP.png")
                );
    }
    // function that given a list of pokemon types will return a list of the corresponding urls
    public List<String> getTypeUrls (List<String> types){
        List<String> result = new ArrayList<>();
        for (String type : types){
            result.add(typesUrlMap.get(type.toLowerCase()));
        }
        return result;
    }
}
