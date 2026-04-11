package by.language.platform.exception;

public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String title) {
        super("Курс с названием '" + title + "' уже существует");
    }
}