package org.woahoverflow.chad.framework;

public class Player {
    private int playerHealth;
    private int swordHealth;
    private int armorHealth;

    public Player()
    {
        playerHealth = 10;
        swordHealth = 10;
        armorHealth = 10;
    }

    public Player(int playerHealth, int swordHealth, int armorHealth)
    {
        this.playerHealth = playerHealth;
        this.swordHealth = swordHealth;
        this.armorHealth = armorHealth;
    }

    public int getPlayerHealth()
    {
        return playerHealth;
    }

    public int getSwordHealth()
    {
        return swordHealth;
    }

    public int getArmorHealth()
    {
        return armorHealth;
    }

    public void setPlayerHealth(int health)
    {
        playerHealth = health;
    }

    public void setSwordHealth(int health)
    {
        swordHealth = health;
    }

    public void setArmorHealth(int health)
    {
        armorHealth = health;
    }

    public void incrementPlayerHealth(int val)
    {
        playerHealth += val;
    }

    public void incrementSwordHealth(int val)
    {
        swordHealth += val;
    }

    public void incrementArmorHealth(int val)
    {
        armorHealth += val;
    }

    public void decrementPlayerHealth(int val)
    {
        playerHealth -= val;
    }

    public void decrementSwordHealth(int val)
    {
        swordHealth -= val;
    }

    public void decrementArmorHealth(int val)
    {
        armorHealth -= val;
    }
}
