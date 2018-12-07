package org.woahoverflow.chad.framework;

public class Player {
    private int playerHealth;
    private int swordHealth;
    private int armorHealth;

    public Player()
    {
        this.playerHealth = 10;
        this.swordHealth = 10;
        this.armorHealth = 10;
    }

    public Player(int playerHealth, int swordHealth, int armorHealth)
    {
        this.playerHealth = playerHealth;
        this.swordHealth = swordHealth;
        this.armorHealth = armorHealth;
    }

    public int getPlayerHealth()
    {
        return this.playerHealth;
    }

    public int getSwordHealth()
    {
        return this.swordHealth;
    }

    public int getArmorHealth()
    {
        return this.armorHealth;
    }

    public void setPlayerHealth(int health)
    {
        this.playerHealth = health;
    }

    public void setSwordHealth(int health)
    {
        this.swordHealth = health;
    }

    public void setArmorHealth(int health)
    {
        this.armorHealth = health;
    }

    public void incrementPlayerHealth(int val)
    {
        this.playerHealth += val;
    }

    public void incrementSwordHealth(int val)
    {
        this.swordHealth += val;
    }

    public void incrementArmorHealth(int val)
    {
        this.armorHealth += val;
    }

    public void decrementPlayerHealth(int val)
    {
        this.playerHealth -= val;
    }

    public void decrementSwordHealth(int val)
    {
        this.swordHealth -= val;
    }

    public void decrementArmorHealth(int val)
    {
        this.armorHealth -= val;
    }
}
