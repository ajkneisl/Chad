package org.woahoverflow.chad.core.listener;

import org.woahoverflow.chad.handle.MessageHandler;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.cache.LongMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Punishments
{
    // there's nothing about userkickevent, so I'm just assuming 4j put them both together.
    @EventSubscriber
    public void banEvent(UserBanEvent e) {
        IUser user = new IUser() {
            @Override
            public String getName() {
                return "Discord Banning";
            }

            @Override
            public String getAvatar() {
                return null;
            }

            @Override
            public String getAvatarURL() {
                return null;
            }

            @Override
            public IPresence getPresence() {
                return null;
            }

            @Override
            public String getDisplayName(IGuild iGuild) {
                return null;
            }

            @Override
            public String mention() {
                return null;
            }

            @Override
            public String mention(boolean b) {
                return null;
            }

            @Override
            public String getDiscriminator() {
                return null;
            }

            @Override
            public List<IRole> getRolesForGuild(IGuild iGuild) {
                return null;
            }

            @Override
            public Color getColorForGuild(IGuild iGuild) {
                return null;
            }

            @Override
            public EnumSet<Permissions> getPermissionsForGuild(IGuild iGuild) {
                return null;
            }

            @Override
            public String getNicknameForGuild(IGuild iGuild) {
                return null;
            }

            @Override
            public IVoiceState getVoiceStateForGuild(IGuild iGuild) {
                return null;
            }

            @Override
            public LongMap<IVoiceState> getVoiceStates() {
                return null;
            }

            @Override
            public void moveToVoiceChannel(IVoiceChannel iVoiceChannel) {

            }

            @Override
            public boolean isBot() {
                return false;
            }

            @Override
            public IPrivateChannel getOrCreatePMChannel() {
                return null;
            }

            @Override
            public void addRole(IRole iRole) {

            }

            @Override
            public void removeRole(IRole iRole) {

            }

            @Override
            public boolean hasRole(IRole iRole) {
                return false;
            }

            @Override
            public IDiscordClient getClient() {
                return null;
            }

            @Override
            public IShard getShard() {
                return null;
            }

            @Override
            public IUser copy() {
                return null;
            }

            @Override
            public long getLongID() {
                return 0;
            }
        };
        List<String> l = new ArrayList<>();
        l.add("No Reason");
        new MessageHandler(null).sendPunishLog("Ban", e.getUser(), user, e.getGuild(), l);
    }
}
