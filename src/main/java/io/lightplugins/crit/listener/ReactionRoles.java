package io.lightplugins.crit.listener;

import io.lightplugins.crit.master.LightMaster;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactionRoles extends ListenerAdapter {


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        TextChannel channel = LightMaster.instance.getShardManager().getTextChannelById("1076991560463417494");

        if(!event.getChannel().asTextChannel().equals(channel)) {
            return;
        }

        String emoji = event.getReaction().getEmoji().getAsReactionCode();

        if(emoji.equalsIgnoreCase("cs2logo:1157657152303943720")) {
            if(event.getMember() == null) {
                return;
            }

            Role role = event.getGuild().getRoleById("1262999792380936303");

            if(role == null) {
                System.out.println("Role not found");
                return;
            }

            event.getGuild().addRoleToMember(event.getMember(), role).queue(
                    success -> System.out.println("Role added successfully to " + event.getMember().getUser().getName()),
                    error -> {
                        if (error.getMessage().contains("higher or equal highest role")) {
                            System.out.println("Failed to add role due to role hierarchy issue: " + error.getMessage());
                        } else {
                            System.out.println("Failed to add role: " + error.getMessage());
                        }
                    }
            );

            System.out.println("Role added for " + event.getMember().getUser().getName());
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        TextChannel channel = LightMaster.instance.getShardManager().getTextChannelById("1076991560463417494");

        if (!event.getChannel().asTextChannel().equals(channel)) {
            return;
        }

        String emoji = event.getReaction().getEmoji().getAsReactionCode();

        if (emoji.equalsIgnoreCase("cs2logo:1157657152303943720")) {
            if (event.getMember() == null) {
                return;
            }

            Role role = event.getGuild().getRoleById("1262999792380936303");

            if (role == null) {
                System.out.println("Role not found");
                return;
            }

            event.getGuild().removeRoleFromMember(event.getMember(), role).queue(
                    success -> System.out.println("Role successfully removed from " + event.getMember().getUser().getName()),
                    error -> System.out.println("Failed to remove role: " + error.getMessage())
            );
        }
    }
}
