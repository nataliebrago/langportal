package by.language.platform.exception;

public class CourseAlreadyPurchasedException extends RuntimeException {
    public CourseAlreadyPurchasedException(Long userId, Long courseId) {
        super("Пользователь с ID " + userId + " уже приобрёл курс с ID " + courseId);
    }
}