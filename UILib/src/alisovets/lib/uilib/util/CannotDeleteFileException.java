package alisovets.lib.uilib.util;

import java.io.IOException;

/**
 * Exception is thrown when file deleting is fail 
 *
 */
public class CannotDeleteFileException extends IOException {

	private static final long serialVersionUID = 7316063495897304256L;

	public CannotDeleteFileException() {
		super();
	}

	public CannotDeleteFileException(String message) {
		super(message);
	}

}
