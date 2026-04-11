package by.language.platform.exception;

public class CoursePriceException extends RuntimeException {
    public CoursePriceException() {
        super("Цена курса не может быть пустой");
    }
}