package com.fastcampus.sns.model.entity;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.model.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name="\"post\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"post\" SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at is NULL")
@NoArgsConstructor

public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="title")
    private String title;

    @Column(name="body", columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name="registered_at")
    private Timestamp registeredAt;

    @Column(name="updated_at")
    private Timestamp updatedAt;

    @Column(name="deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from((Instant.now()));
    }

    public static PostEntity of(String title, String body) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setBody(body);
            UserEntity userEntity = new UserEntity();
            userEntity.setUserName("userName");
            userEntity.setPassword("password");
        postEntity.setUser(userEntity);
        return postEntity;
    }
}
