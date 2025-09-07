package dev.a8kj7sea.chatinput.handlers;

import org.bukkit.entity.Player;

import dev.a8kj7sea.chatinput.InputValidationResult;

/**
 * Validates player input and returns a result.
 */
@FunctionalInterface
public interface InputValidator {

	/**
	 * Validates the input provided by a player.
	 *
	 * @param player The player providing the input.
	 * @param input  The raw input string from the player.
	 * @return An {@link InputValidationResult} indicating if the input is valid.
	 */
	InputValidationResult validate(Player player, String input);
}
