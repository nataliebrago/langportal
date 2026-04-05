package by.language.platform.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Покупка: пользователь купил курс за конкретную сумму (вдруг была скидка).
 */
@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Кто купил */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* Какой курс */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /* Сколько реально заплатил (со скидкой или без) */
    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private double paidAmount;

    protected Purchase() {}

    public Purchase(User user, Course course, double paidAmount) {
        this.user = user;
        this.course = course;
        this.paidAmount = paidAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase purchase)) return false;
        return id != null && Objects.equals(id, purchase.id);
    }
    @Override
    public int hashCode() { return getClass().hashCode(); }
    @Override
    public String toString() {
        return "Purchase{id=" + id + ", user=" + user.getEmail() + ", course=" + course.getTitle() + ", paid=" + paidAmount + '}';
    }
}