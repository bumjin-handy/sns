package com.fastcampus.sns.model.entity;

import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name="\"alarm\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE \"alarm\" SET removed_at = NOW() where id = ?")
@SQLRestriction("removed_at is NULL")
@NoArgsConstructor

public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //  알림을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    //https://thorben-janssen.com/persist-postgresqls-jsonb-data-type-hibernate/
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private AlarmArgs args;

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

    public static AlarmEntity of(AlarmType alarmType, AlarmArgs args, UserEntity user) {
        AlarmEntity entity = new AlarmEntity();
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        entity.setUser(user);
        return entity;
    }
}
