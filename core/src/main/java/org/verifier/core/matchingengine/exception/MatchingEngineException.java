package org.verifier.core.matchingengine.exception;

public class MatchingEngineException extends RuntimeException {
    private static final long serialVersionUID = -2180357857089925808L;

	public MatchingEngineException(String message) {
        super(message);
    }

    public MatchingEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
