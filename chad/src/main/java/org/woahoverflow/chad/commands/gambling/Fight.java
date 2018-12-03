package org.woahoverflow.chad.commands.gambling;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.woahoverflow.chad.framework.Command;
import org.woahoverflow.chad.framework.handle.DatabaseHandler;
import org.woahoverflow.chad.framework.handle.MessageHandler;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

public class Fight implements Command.Class
{
    @Override
    public Runnable run(MessageReceivedEvent e, List<String> args) {
        return () -> {
            MessageHandler messageHandler = new MessageHandler(e.getChannel());
            
            if (!(args.size() >= 2))
            {
                messageHandler.sendError(MessageHandler.INVALID_ARGUMENTS);
                return;
            }
            
            // Arguments are removed during user building, so I put them here :)
            final String arg0 = args.get(0);

            // Opponent
            IUser opponentUser = null;

            // Builds the user from the arguments
            if (e.getMessage().getMentions().isEmpty())
            {
                StringBuilder stringBuilder = new StringBuilder();
                args.remove(0);
                for (String s : args)
                {
                    stringBuilder.append(s).append(' ');
                    if (!e.getGuild().getUsersByName(stringBuilder.toString().trim()).isEmpty())
                    {
                        opponentUser = e.getGuild().getUsersByName(stringBuilder.toString().trim()).get(0);
                        break;
                    }
                }

                // Checks if the loop actually found a user
                if (opponentUser == null)
                {
                    messageHandler.sendError("Invalid User!");
                    return;
                }
            }
            else {
                // If there's a mention use that instead
                if (args.get(1).contains(e.getMessage().getMentions().get(0).getStringID()))
                {
                    opponentUser = e.getMessage().getMentions().get(0);
                }
                else {
                    messageHandler.sendError("Invalid User!");
                    return;
                }
            }

            // only used once, but thanks lamda
            final IUser user = opponentUser;

            // Sends the invitation message
            IMessage acceptMessage = RequestBuffer
                .request(() -> e.getChannel().sendMessage("Do you accept `" + e.getAuthor().getName() + "`'s challenge, `" + user.getName() + "`?")).get();

            // Creates a request buffer and reacts with the Y and N emojis
            RequestBuilder rb = new RequestBuilder(e.getClient());
            rb.shouldBufferRequests(true);
            rb.doAction(() -> {
                acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDFE")); // Y
                return true;
            }).andThen(() -> {
                acceptMessage.addReaction(ReactionEmoji.of("\uD83C\uDDF3")); // N
                return true;
            }).execute(); // Executes

            // Assigns variables
            boolean reacted = true;
            int timeout = 0;

            while (reacted)
            {
                // If it's been 10 seconds, exit
                if (timeout == 10)
                {
                    messageHandler.sendError('`' +user.getName()+"` didn't respond in time!");
                    return;
                }

                // Sleeps a second so it doesn't go so fast
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                // Increases the timeout value
                timeout++;

                // Gets both reactions
                IReaction yReaction = RequestBuffer.request(() -> acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDFE"))).get();
                IReaction nReaction = RequestBuffer.request(() -> acceptMessage.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF3"))).get();

                // Checks if the user reacted to the Y
                if (yReaction.getUserReacted(user))
                {
                    reacted = false;
                }

                // Checks if the user reacted with the N
                if (nReaction.getUserReacted(user))
                {
                    messageHandler.send("User Denied!", "CoinFlip");
                    return;
                }
            }

            // Calculates the Bet
            long bet;
            try {
                bet = Long.parseLong(arg0);
            } catch (NumberFormatException throwaway) {
                messageHandler.sendError("Invalid Bet!");
                return;
            }

            if (!(bet > 0))
            {
                messageHandler.sendError("Invalid Number!");
                return;
            }

            // Gets the author's balance
            long balance = (long) DatabaseHandler.handle
                .get(e.getGuild(), e.getAuthor().getStringID() + "_balance");

            // Checks if the user's bet is bigger than the balance.
            if (bet > balance)
            {
                messageHandler.sendError("Your bet is too large!");
                return;
            }

            // Gets the opponent's balance
            long opponentBalance = (long) DatabaseHandler.handle
                .get(e.getGuild(), opponentUser.getStringID() + "_balance");

            // Checks if the bet's bigger than the opponent's balance
            if (bet > opponentBalance)
            {
                messageHandler.sendError("Your bet is too large for `"+opponentUser.getName()+"`!");
                return;
            }

            // Checks if the author's balance is too big
            if (bet+balance < 0)
            {
                messageHandler.sendError("Your balance is too big!\nPlease report this on https://woahoverflow.org/forums");
                return;
            }

            // Checks if the opponent's balance is too big
            if (bet+opponentBalance < 0)
            {
                messageHandler.sendError('`' +opponentUser.getName()+"`'s balance is too big!\nPlease report this on https://woahoverflow.org/forums");
                return;
            }

            // Creates the fight message
            IMessage scene = RequestBuffer.request(() -> e.getChannel().sendMessage(
                '`' +e.getAuthor().getName()+"` **HP** `100`\n"
                    + '`' +user.getName()+"` **HP** `100`\n"
                    + "\n**TURN** `"+e.getAuthor().getName()+ '`'
                    + "\n\n**A** Attack User\n"
                    + "**2** Shield")).get();

            // Adds the reactions
            RequestBuilder requestBuilder = new RequestBuilder(e.getClient());
            requestBuilder.shouldBufferRequests(true);
            requestBuilder.doAction(() -> {
                scene.addReaction(ReactionEmoji.of("\uD83C\uDD70")); // Large red A
                return true;
            }).andThen(() -> {
                scene.addReaction(ReactionEmoji.of("\uD83C\uDDF8")); // S
                return true;
            }).execute(); // Executes

            // Turn 0 is the author's turn, turn 1 is the opponent's turn;
            int turn = 0;
            // If the game isn't completed
            boolean gameCompleted = false;
            // If either of the users shielded, this is where their damage reduction goes
            int authorDamageReduction = 0;
            int opponentDamageReduction = 0;
            // The users' health
            int authorHealth = 100;
            int opponentHealth = 100;
            // The users' damage
            int authorSwordHealth = 100;
            int opponentSwordHealth = 100;
            int authorDamage = 20;
            int opponentDamage = 20;
            // The users' shield
            int authorShieldHealth = 100;
            int opponentShieldHealth = 100;

            // While the game is still going on ->
            while (!gameCompleted)
            {
                // if either of the user's have died, break from the loop
                if (authorHealth == 0 || opponentHealth == 0) break;

                /*
                The author's turn
                 */

                // The user's reacted action
                int authorAction = 0;
                // Timeout
                int authorTimeout = 0;

                // The author's action
                while (true)
                {
                    // Timeout if it's been 10 seconds
                    if (authorTimeout == 10)
                    {
                        messageHandler.sendError('`' +e.getAuthor().getName()+"` didn't react fast enough!");
                        return;
                    }
                    authorTimeout++;


                    // Wait a second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    // Gets the attack reactions (large a)
                    IReaction attackReaction = RequestBuffer.request(() -> scene.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDD70"))).get();
                    // Gets the shield reactions (s)
                    IReaction shieldReaction = RequestBuffer.request(() -> scene.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF8"))).get();

                    // Re adds the reactions
                    RequestBuilder requestBuilder1 = new RequestBuilder(e.getClient());
                    requestBuilder1.shouldBufferRequests(true);
                    requestBuilder1.doAction(() -> {
                        scene.addReaction(ReactionEmoji.of("\uD83C\uDD70")); // Large red A
                        return true;
                    }).andThen(() -> {
                        scene.addReaction(ReactionEmoji.of("\uD83C\uDDF8")); // S
                        return true;
                    }).execute(); // Executes

                    // If the author reacted with the attack, set them to that
                    if (attackReaction.getUserReacted(e.getAuthor()))
                    {
                        authorAction = 1;
                        break;
                    }

                    // If the author reacted with the shield, set them to that
                    if (shieldReaction.getUserReacted(e.getAuthor()))
                    {
                        authorAction = 2;
                        break;
                    }
                }

                // Do the actions

                // Damage
                if (authorAction == 1)
                {
                    // Get an amount of health to remove from the author's sword
                    int damageToSword = new SecureRandom().nextInt(20);

                    // Remove the health from the sword
                    authorSwordHealth =- damageToSword;

                    // Makes sure the sword isn't dead
                    if (authorSwordHealth <= 0)
                    {

                        // Remove the damage
                        opponentHealth -= authorDamage;

                        // If the opponent had a shield
                        boolean hasShield = false;

                        // Checks if the author had a shield
                        if (opponentDamageReduction > 0)
                        {
                            // Gives back the health that the shield does
                            opponentHealth += opponentDamageReduction;

                            hasShield = true;
                        }

                        // Makes sure the values aren't below 0
                        if (opponentHealth < 0)
                        {
                            opponentHealth = 0;
                        }

                        if (authorHealth < 0)
                        {
                            authorHealth = 0;
                        }

                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;
                        final int finalOpponentReduction = opponentDamageReduction;

                        // Update the message, and changes if the opponent had a shield
                        if (!hasShield)
                        {
                            RequestBuffer.request(() -> scene.edit(
                                '`' +e.getAuthor().getName()
                                    +"` **HP** `"+finalAuthorHealth+"`\n"
                                    + '`' +user.getName()+"` **HP** `"+ finalOpponentHealth +"`\n"
                                    + "\n**TURN** `"+user.getName()+ '`'
                                    + "\n\n**A** Attack User\n"
                                    + "**2** Shield"));
                        }
                        else {
                            RequestBuffer.request(() -> scene.edit(
                                "**WOW** `"+user.getName()+"`'s shield blocked `"+finalOpponentReduction+"` damage! \n`" +e.getAuthor().getName()
                                    +"` **HP** `"+finalAuthorHealth+"`\n"
                                    + '`' +user.getName()+"` **HP** `"+ finalOpponentHealth +"`\n"
                                    + "\n**TURN** `"+user.getName()+ '`'
                                    + "\n\n**A** Attack User\n"
                                    + "**2** Shield"));
                        }
                    }
                    else {
                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;
                        RequestBuffer.request(() -> scene.edit(
                            "**OH NO** `"+e.getAuthor().getName()+"`'s sword broke! \n\n`"+e.getAuthor().getName()+"` **HP** `"+finalAuthorHealth+"`\n"
                                + '`' +user.getName()+"` **HP** `"+ finalOpponentHealth +"`\n"
                                + "\n**TURN** `"+user.getName()+ '`'
                                + "\n\n**A** Attack User\n"
                                + "**2** Shield"));
                    }
                }

                // Shield
                if (authorAction == 2)
                {
                    // Get an amount of health to remove from the author's shield
                    int damageToShield = new SecureRandom().nextInt(20);

                    // Remove the health from the shield
                    authorShieldHealth =- damageToShield;

                    // Makes sure the shield isn't dead
                    if (authorShieldHealth <= 0)
                    {
                        // Add the damage reduction
                        authorDamageReduction = new SecureRandom().nextInt(50);

                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;

                        // Update the message
                        RequestBuffer.request(() -> scene.edit(
                            '`' +e.getAuthor().getName()
                                +"` **HP** `"+finalAuthorHealth+"`\n"
                                + '`' +user.getName()+"` **HP** `"+ finalOpponentHealth +"`\n"
                                + "\n**TURN** `"+user.getName()+ '`'
                                + "\n\n**A** Attack User\n"
                                + "**2** Shield"));
                    }
                    else {
                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;
                        RequestBuffer.request(() -> scene.edit(
                            "**OH NO** `"+e.getAuthor().getName()+"`'s shield broke! \n\n`"+e.getAuthor().getName()+"` **HP** `"+finalAuthorHealth+"`\n"
                                + '`' +user.getName()+"` **HP** `"+ finalOpponentHealth +"`\n"
                                + "\n**TURN** `"+user.getName()+ '`'
                                + "\n\n**A** Attack User\n"
                                + "**2** Shield"));
                    }
                }

                /*
                The opponent's turn
                 */

                // Clears the reactions
                RequestBuffer.request(scene::removeAllReactions);

                // The user's reacted action
                int opponentAction = 0;
                // Timeout
                int opponentTimeout = 0;

                // The opponent's action
                while (true) {
                    // Timeout if it's been 10 seconds
                    if (opponentTimeout == 10) {
                        messageHandler.sendError(
                            '`' + user.getName() + "` didn't react fast enough!");
                        return;
                    }
                    opponentTimeout++;

                    // Wait a second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    // Gets the attack reactions (large a)
                    IReaction attackReaction = RequestBuffer.request(
                        () -> scene.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDD70"))).get();
                    // Gets the shield reactions (s)
                    IReaction shieldReaction = RequestBuffer.request(
                        () -> scene.getReactionByEmoji(ReactionEmoji.of("\uD83C\uDDF8"))).get();

                    // Re adds the reactions
                    RequestBuilder requestBuilder1 = new RequestBuilder(e.getClient());
                    requestBuilder1.shouldBufferRequests(true);
                    requestBuilder1.doAction(() -> {
                        scene.addReaction(ReactionEmoji.of("\uD83C\uDD70")); // Large red A
                        return true;
                    }).andThen(() -> {
                        scene.addReaction(ReactionEmoji.of("\uD83C\uDDF8")); // S
                        return true;
                    }).execute(); // Executes

                    // If the opponent reacted with the attack, set them to that
                    if (attackReaction.getUserReacted(user)) {
                        opponentAction = 1;
                        break;
                    }

                    // If the opponent reacted with the shield, set them to that
                    if (shieldReaction.getUserReacted(user)) {
                        opponentAction = 2;
                        break;
                    }
                }

                // Do the actions

                // Damage
                if (opponentAction == 1) {
                    // Get an amount of health to remove from the author's sword
                    int damageToSword = new SecureRandom().nextInt(20);

                    // Remove the health from the sword
                    opponentSwordHealth = -damageToSword;

                    // Makes sure the sword isn't dead
                    if (opponentSwordHealth <= 0) {

                        // Remove the damage
                        authorHealth -= opponentDamage;

                        // If the opponent had a shield
                        boolean hasShield = false;

                        // Checks if the author had a shield
                        if (authorDamageReduction > 0) {
                            // Gives back the health that the shield does
                            authorHealth += authorDamageReduction;

                            hasShield = true;
                        }

                        // Makes sure the values aren't below 0
                        if (opponentHealth < 0) {
                            opponentHealth = 0;
                        }

                        if (authorHealth < 0) {
                            authorHealth = 0;
                        }

                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;
                        final int finalAuthorReduction = authorDamageReduction;

                        // Update the message, and changes if the opponent had a shield
                        if (!hasShield) {
                            RequestBuffer.request(() -> scene.edit(
                                '`' + e.getAuthor().getName()
                                    + "` **HP** `" + finalAuthorHealth + "`\n"
                                    + '`' + user.getName() + "` **HP** `" + finalOpponentHealth
                                    + "`\n"
                                    + "\n**TURN** `" + e.getAuthor().getName() + '`'
                                    + "\n\n**A** Attack User\n"
                                    + "**2** Shield"));
                        } else {
                            RequestBuffer.request(() -> scene.edit(
                                "**WOW** `" + e.getAuthor().getName() + "`'s shield blocked `"
                                    + finalAuthorReduction + "` damage! \n`" + e.getAuthor()
                                    .getName()
                                    + "` **HP** `" + finalAuthorHealth + "`\n"
                                    + '`' + user.getName() + "` **HP** `" + finalOpponentHealth
                                    + "`\n"
                                    + "\n**TURN** `" + e.getAuthor().getName() + '`'
                                    + "\n\n**A** Attack User\n"
                                    + "**2** Shield"));
                        }
                    } else {
                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;
                        RequestBuffer.request(() -> scene.edit(
                            "**OH NO** `" + user.getName() + "`'s sword broke! \n\n`"
                                + e.getAuthor().getName() + "` **HP** `" + finalAuthorHealth
                                + "`\n"
                                + '`' + user.getName() + "` **HP** `" + finalOpponentHealth
                                + "`\n"
                                + "\n**TURN** `" + e.getAuthor().getName() + '`'
                                + "\n\n**A** Attack User\n"
                                + "**2** Shield"));
                    }
                }

                // Shield
                if (opponentAction == 2) {
                    // Get an amount of health to remove from the opponent's shield
                    int damageToShield = new SecureRandom().nextInt(20);

                    // Remove the health from the shield
                    opponentShieldHealth = -damageToShield;

                    // Makes sure the shield isn't dead
                    if (opponentShieldHealth <= 0) {
                        // Add the damage reduction
                        opponentDamageReduction = new SecureRandom().nextInt(50);

                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;

                        // Update the message
                        RequestBuffer.request(() -> scene.edit(
                            '`' + e.getAuthor().getName()
                                + "` **HP** `" + finalAuthorHealth + "`\n"
                                + '`' + user.getName() + "` **HP** `" + finalOpponentHealth
                                + "`\n"
                                + "\n**TURN** `" + e.getAuthor().getName() + '`'
                                + "\n\n**A** Attack User\n"
                                + "**2** Shield"));
                    } else {
                        final int finalOpponentHealth = opponentHealth;
                        final int finalAuthorHealth = authorHealth;
                        RequestBuffer.request(() -> scene.edit(
                            "**OH NO** `" + user.getName()+ "`'s shield broke! \n\n`"
                                + e.getAuthor().getName() + "` **HP** `" + finalAuthorHealth
                                + "`\n"
                                + '`' + user.getName() + "` **HP** `" + finalOpponentHealth
                                + "`\n"
                                + "\n**TURN** `" + user.getName() + '`'
                                + "\n\n**A** Attack User\n"
                                + "**2** Shield"));
                    }
               }
            }

            messageHandler.sendError(opponentHealth + " : " + authorHealth);
        };
    }

    @Override
    public Runnable help(MessageReceivedEvent e) {
        return null;
    }
}
