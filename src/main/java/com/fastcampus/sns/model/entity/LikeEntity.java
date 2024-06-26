package com.fastcampus.sns.model.entity;

import com.fastcampus.sns.model.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name="\"like\"")
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"like\" SET removed_at = NOW() where id = ?")
@SQLRestriction("removed_at is NULL")
@NoArgsConstructor

public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name="registered_at")
    private Timestamp registeredAt;

    @Column(name="updated_at")
    private Timestamp updatedAt;

    @Column(name="removed_at")
    private Timestamp removedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from((Instant.now()));
    }

    public static LikeEntity of(UserEntity userEntity, PostEntity postEntity) {
        LikeEntity entity = new LikeEntity();
        entity.setUser(userEntity);
        entity.setPost(postEntity);
        return entity;
    }
}
