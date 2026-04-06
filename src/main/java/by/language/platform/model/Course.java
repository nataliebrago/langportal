package by.language.platform.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Курс (языковой) с фиксированной ценой.
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Название курса */
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private double price;

    protected Course() {
    }

    public Course(String title, double price) {
        this.title = title;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return id != null && Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Course{id=" + id + ", title='" + title + '\'' + ", price=" + price + '}';
    }
}