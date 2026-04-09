package by.language.platform.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal paidAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    protected Purchase() {
    }

    public Purchase(User user, Course course, BigDecimal paidAmount, LocalDateTime created) {
        this.user = user;
        this.course = course;
        this.paidAmount = paidAmount;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", user=" + user +
                ", course=" + course +
                ", paidAmount=" + paidAmount +
                ", created=" + created +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase purchase)) return false;
        return Objects.equals(getId(), purchase.getId()) &&
                Objects.equals(getUser(), purchase.getUser()) &&
                Objects.equals(getCourse(), purchase.getCourse()) &&
                Objects.equals(getPaidAmount(), purchase.getPaidAmount()) &&
                Objects.equals(getCreated(), purchase.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getCourse(), getPaidAmount(), getCreated());
    }

}