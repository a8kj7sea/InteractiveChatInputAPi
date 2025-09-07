package dev.a8kj7sea.chatinput;

import lombok.Getter;

/**
 * Represents the result of an input validation check. This class provides a
 * clear and standardized way to convey whether input is valid and, if not, the
 * reason for the failure.
 */
@Getter
public class InputValidationResult {

	/**
	 * {@code true} if the input is valid, {@code false} otherwise.
	 */
	private final boolean valid;

	/**
	 * The reason for validation failure. Null if the input is valid.
	 */
	private final String reason;

	/**
	 * Private constructor to enforce the use of static factory methods.
	 *
	 * @param valid  Whether the input is valid.
	 * @param reason The reason for failure, or null if valid.
	 */
	private InputValidationResult(boolean valid, String reason) {
		this.valid = valid;
		this.reason = reason;
	}

	/**
	 * Creates a successful validation result.
	 *
	 * @return A new {@code InputValidationResult} instance indicating success.
	 */
	public static InputValidationResult ok() {
		return new InputValidationResult(true, null);
	}

	/**
	 * Creates a failed validation result with a specified reason.
	 *
	 * @param reason The message explaining why the validation failed.
	 * @return A new {@code InputValidationResult} instance indicating failure.
	 */
	public static InputValidationResult fail(String reason) {
		return new InputValidationResult(false, reason);
	}
}
