package ie.tcd.scss.apapung.Domain;

/**
 * Represents a statistical value for a Pokémon's attribute (e.g., HP, Attack).
 */
public class Stat {

    // Base value of the stat for a Pokémon.
    private int base_stat;

    /**
     * Getter method to retrieve the base_stat value.
     * 
     * @return the base_stat value.
     */
    public int getBase_stat() {
        return base_stat;
    }

    /**
     * Setter method to set the base_stat value.
     * 
     * @param base_stat the value to set for the base_stat.
     */
    public void setBase_stat(int base_stat) {
        this.base_stat = base_stat; // Assigns the provided value to base_stat.
    }

}
