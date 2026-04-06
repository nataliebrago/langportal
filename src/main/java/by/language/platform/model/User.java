package by.language.platform.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Пользователь платформы.
 * registered == true — подтвердил e-mail и может получать скидки.
 */
@Entity
@Table(name = "users")
public class User {

    /* Уникальный идентификатор (генерируется БД) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Электронная почта = логин. Должна быть уникальна */
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /* Хэш пароля*/
    @Column(nullable = false, length = 120)
    private String password;

    /* Фамилия */
    @Column(length = 120)
    private String surname;

    /* Имя */
    @Column(length = 120)
    private String name;


    /* Флаг «e-mail подтверждён» → даём скидки */
    @Column(name = "registered")
    private boolean registered;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
    }


    protected User() {
    }

    public User(String email, String password, String surname, String name, boolean registered) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.registered = registered;
    }

    /* ===== Getters / Setters ===== */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return isRegistered() == user.isRegistered() &&
                Objects.equals(getId(), user.getId()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getSurname(), user.getSurname()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getCreated(), user.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getPassword(), getSurname(), getName(), isRegistered(), getCreated());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", registered=" + registered +
                ", created=" + created +
                '}';
    }
}