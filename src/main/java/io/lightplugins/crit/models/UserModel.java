package io.lightplugins.crit.models;

import net.dv8tion.jda.api.entities.Member;

import java.util.Date;

public class UserModel {

    private String discordUserID;
    private int coins;
    private double exp;
    private double messages;
    private Date firstJoin;
    private Date lastJoin;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getDiscordUserID() {
        return discordUserID;
    }

    public void setDiscordUserID(String discordUserID) {
        this.discordUserID = discordUserID;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public Date getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(Date firstJoin) {
        this.firstJoin = firstJoin;
    }

    public Date getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(Date lastJoin) {
        this.lastJoin = lastJoin;
    }

    public double getMessages() {
        return messages;
    }

    public void setMessages(double messages) {
        this.messages = messages;
    }
    
}
