package org.woahoverflow.chad.framework;

/**
 * @author sho, codebasepw
 * @since 0.7.0
 */
public class Player {
    private int playerHealth;
    private int swordHealth;
    private int armorHealth;
    private long balance;

    public Player(int playerHealth, int swordHealth, int armorHealth, long balance)
    {
        this.playerHealth = playerHealth;
        this.swordHealth = swordHealth;
        this.armorHealth = armorHealth;
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
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
