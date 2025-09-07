package dev.a8kj7sea.chatinput.handlers;

import org.bukkit.entity.Player;

/**
 * Handles execution when player input is received and validated.
 */
@FunctionalInterface
public interface InputReceiver {

	/**
	 * Called when a player provides a valid input.
	 *
	 * @param player The player who provided the input.
	 * @param input  The validated input string from the player.
	 */
	void onReceive(Player player, String input);
}
