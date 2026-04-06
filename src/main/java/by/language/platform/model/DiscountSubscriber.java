package by.language.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * E-mail, подписанный на рассылку «новых скидок».
 */
@Entity
@Table(name = "discount_subscribers")
public class DiscountSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Адрес, куда шлём анонсы */
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }

    protected DiscountSubscriber() {}

    public DiscountSubscriber(String email) { this.email = email; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountSubscriber that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getCreated(), that.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getCreated());
    }

    @Override
    public String toString() {
        return "DiscountSubscriber{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", created=" + created +
                '}';
    }
}