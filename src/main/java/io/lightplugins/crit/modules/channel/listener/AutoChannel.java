package io.lightplugins.crit.modules.channel.listener;


import io.lightplugins.crit.modules.channel.LightChannel;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoChannel extends ListenerAdapter {



    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

        boolean enabled = LightChannel.instance.getAutoChannelConfig().getBoolean("autochannel.enable");

        //  Check if the AutoChannel module is enabled
        if(!enabled) {
            return;
        }

        long fiveChannel = LightChannel.instance.getAutoChannelConfig().getLong(
                "autochannel.join-channels.five");
        long fourChannel = LightChannel.instance.getAutoChannelConfig().getLong(
                "autochannel.join-channels.four");
        long threeChannel = LightChannel.instance.getAutoChannelConfig().getLong(
                "autochannel.join-channels.three");
        long twoChannel = LightChannel.instance.getAutoChannelConfig().getLong(
                "autochannel.join-channels.two");
        long categoryId = LightChannel.instance.getAutoChannelConfig().getLong(
                "autochannel.category-id");

        AudioChannel joinedChannel = event.getNewValue();
        AudioChannel leftChannel = event.getOldValue();
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (joinedChannel != null) {
            Category category = guild.getCategoryById(categoryId);
            if (category != null) {
                int userLimit = 0;
                if (joinedChannel.getId().equals(String.valueOf(fiveChannel))) {
                    userLimit = 5;
                } else if (joinedChannel.getId().equals(String.valueOf(fourChannel))) {
                    userLimit = 4;
                } else if (joinedChannel.getId().equals(String.valueOf(threeChannel))) {
                    userLimit = 3;
                } else if (joinedChannel.getId().equals(String.valueOf(twoChannel))) {
                    userLimit = 2;
                }



                if (userLimit > 0) {
                    guild.createVoiceChannel(LightChannel.instance.getAutoChannelName() + member.getEffectiveName(), category)
                            .syncPermissionOverrides()
                            .setBitrate(guild.getMaxBitrate())
                            .setUserlimit(userLimit)
                            .queue(voiceChannel -> {
                                guild.moveVoiceMember(member, voiceChannel).queue();
                            });
                    LightPrinter.print("[AUTOCHANNEL] Created auto channel for " +
                            member.getEffectiveName() + " in " + category.getName() +
                            " with user limit " + userLimit);
                }
            }
        }

        if (leftChannel == null) {
            return;
        }

        // Delete empty auto channel if the last member left
        if (leftChannel.getName().contains(LightChannel.instance.getAutoChannelName()) && leftChannel.getMembers().isEmpty()) {
            leftChannel.delete().queue();
            LightPrinter.print("[AUTOCHANNEL] Deleted empty auto channel: " + leftChannel.getName());
        }
    }

    // Clean up empty auto channels on bot startup, if the bot is a long time offline.
    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();
        for (AudioChannel channel : guild.getVoiceChannels()) {
            if (channel.getName().contains(LightChannel.instance.getAutoChannelName()) && channel.getMembers().isEmpty()) {
                LightPrinter.print("[AUTOCHANNEL] Detected empty auto channel on bot startup. Try to delete: " + channel.getName());
                channel.delete().queue();
                LightPrinter.print("[AUTOCHANNEL] Deleted empty auto channel successfully: " + channel.getName());
            }
        }
    }
}