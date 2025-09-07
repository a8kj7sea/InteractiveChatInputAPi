package dev.a8kj7sea.chatinput.context;

import dev.a8kj7sea.chatinput.handlers.InputCanceller;
import dev.a8kj7sea.chatinput.handlers.InputReceiver;
import dev.a8kj7sea.chatinput.handlers.InputValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents an active input context for a player, containing all the necessary
 * handlers and data for an interactive input session.
 */
@RequiredArgsConstructor
@Getter
public class InputContext {

	/**
	 * A unique identifier for the context, used to distinguish it from others.
	 */
	private final String name;

	/**
	 * The player associated with this input context.
	 */
	private final Player player;

	/**
	 * The initial message or question to send to the player.
	 */
	private final String prompt;

	/**
	 * The handler that processes the player's input upon successful validation.
	 */
	private final InputReceiver receiver;

	/**
	 * An optional validator to check the player's input.
	 */
	private final InputValidator validator;

	/**
	 * An optional handler to execute when the input is cancelled.
	 */
	private final InputCanceller canceller;

	/**
	 * An optional task that will automatically cancel the context after a delay.
	 */
	private final BukkitTask timeoutTask;

	/**
	 * The keyword the player can type to manually cancel the input.
	 */
	private final String cancelKeyword;

}
