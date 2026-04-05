package by.language.platform.model;

import jakarta.persistence.*;
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

    protected DiscountSubscriber() {}

    public DiscountSubscriber(String email) { this.email = email; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountSubscriber that)) return false;
        return id != null && Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return getClass().hashCode(); }
    @Override
    public String toString() { return "DiscountSubscriber{id=" + id + ", email='" + email + '\'' + '}'; }
}