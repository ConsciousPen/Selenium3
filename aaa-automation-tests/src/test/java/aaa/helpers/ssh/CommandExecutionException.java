package aaa.helpers.ssh;

import toolkit.exceptions.IstfException;

public class CommandExecutionException extends IstfException {
	private static final long serialVersionUID = -8409245193645164552L;

	CommandExecutionException(String message) {
		super(message);
	}

	CommandExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	CommandExecutionException(Throwable cause) {
		super(cause);
	}
}
