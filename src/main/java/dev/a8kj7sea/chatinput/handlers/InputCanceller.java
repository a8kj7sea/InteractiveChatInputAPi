package dev.a8kj7sea.chatinput.handlers;

import org.bukkit.entity.Player;

/**
 * A functional interface to handle the logic when a player cancels input.
 */
@FunctionalInterface
public interface InputCanceller {

	/**
	 * Called when the player cancels the input process.
	 *
	 * @param player The player who cancelled the input.
	 */
	void onCancel(Player player);
}
