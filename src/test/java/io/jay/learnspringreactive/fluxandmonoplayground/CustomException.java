package io.jay.learnspringreactive.fluxandmonoplayground;

public class CustomException extends Throwable {
    private String message;

    public CustomException(Throwable exception) {

        this.message = exception.getMessage();

    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
