package dev.a8kj7sea.chatinput.session;

import dev.a8kj7sea.chatinput.context.InputContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Represents a player's session, holding the currently active input context.
 * <p>
 * This class adheres to the Single Responsibility Principle (SRP) by focusing
 * solely on managing the session's state (the active context). It does not
 * handle the lifecycle of the context itself (e.g., cancelling timeout tasks),
 * leaving that responsibility to the API that manages the sessions.
 */
@Getter
@RequiredArgsConstructor
public class PlayerSession {

	/**
	 * The player associated with this session.
	 */
	private final Player player;

	/**
	 * The currently active input context for the player. Null if none is active.
	 */
	private InputContext activeContext;

	/**
	 * Sets a new active context for the session, returning the old one.
	 * <p>
	 * This design allows the caller to manage the lifecycle of the old context,
	 * such as cancelling any associated tasks, before it's discarded.
	 *
	 * @param newContext The new input context to set.
	 * @return The previously active InputContext, or null if there was none.
	 */
	public InputContext setActiveContext(InputContext newContext) {
		InputContext oldContext = this.activeContext;
		this.activeContext = newContext;
		return oldContext;
	}

	/**
	 * Clears the active input context and returns it.
	 *
	 * @return The {@link InputContext} that was just cleared, or null if none was
	 *         active.
	 */
	public InputContext clearContext() {
		return setActiveContext(null);
	}

	/**
	 * Checks if there is an active input context in this session.
	 *
	 * @return {@code true} if an active context exists, {@code false} otherwise.
	 */
	public boolean hasActiveContext() {
		return activeContext != null;
	}
}
