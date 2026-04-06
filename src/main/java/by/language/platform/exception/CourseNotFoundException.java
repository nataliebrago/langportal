package by.language.platform.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к курсу, который не существует.
 */
public class CourseNotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением по умолчанию.
     */
    public CourseNotFoundException() {
        super("Курс не найден");
    }

    /**
     * Создаёт исключение с указанием ID курса.
     *
     * @param courseId идентификатор курса, который не был найден
     */
    public CourseNotFoundException(Long courseId) {
        super("Курс с ID " + courseId + " не найден");
    }
}