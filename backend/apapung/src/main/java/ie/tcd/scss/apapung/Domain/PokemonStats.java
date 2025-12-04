package ie.tcd.scss.apapung.Domain;

import java.util.List;

/**
 * Represents detailed statistics of a Pokémon, including its name, types, 
 * dex number, individual stats, and sprite URL.
 */
public class PokemonStats {

    // Name of the Pokémon.
    private String name;

    // List of types associated with the Pokémon (e.g., Fire, Water).
    private List<String> types;

    // The Pokédex number of the Pokémon.
    private int dexNumber;

    // List of individual stats for the Pokémon (e.g., HP, Attack).
    private List<Stat> stats;

    // URL of the Pokémon's sprite image.
    private String spriteURL;

    /**
     * Getter method to retrieve the name of the Pokémon.
     * 
     * @return the name of the Pokémon.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method to set the name of the Pokémon.
     * 
     * @param name the name to set for the Pokémon.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method to retrieve the types of the Pokémon.
     * 
     * @return a list of types associated with the Pokémon.
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * Setter method to set the types of the Pokémon.
     * 
     * @param types a list of types to set for the Pokémon.
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * Getter method to retrieve the Pokédex number of the Pokémon.
     * 
     * @return the Pokédex number.
     */
    public int getDexNumber() {
        return dexNumber;
    }

    /**
     * Setter method to set the Pokédex number of the Pokémon.
     * 
     * @param dexNumber the Pokédex number to set.
     */
    public void setDexNumber(int dexNumber) {
        this.dexNumber = dexNumber;
    }

    /**
     * Getter method to retrieve the stats of the Pokémon.
     * 
     * @return a list of stats for the Pokémon.
     */
    public List<Stat> getStats() {
        return stats;
    }

    /**
     * Setter method to set the stats of the Pokémon.
     * 
     * @param stats a list of stats to set for the Pokémon.
     */
    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

    /**
     * Getter method to retrieve the sprite URL of the Pokémon.
     * 
     * @return the URL of the Pokémon's sprite.
     */
    public String getSpriteURL() {
        return spriteURL;
    }

    /**
     * Setter method to set the sprite URL of the Pokémon.
     * 
     * @param spriteURL the sprite URL to set.
     */
    public void setSpriteURL(String spriteURL) {
        this.spriteURL = spriteURL;
    }
}
