package gachon.bridge.userservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userIdx")
    private UUID pk;

    @Column(nullable = false, length = 45, unique = true)
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pw;

    @Column(nullable = false, length = 45)
    private String email;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Date createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Date updatedAt;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0", name = "status")
    private Boolean expired; // data 삭제 처리 여부

    public User(String userId, String pw, String email) {
        this.userId = userId;
        this.pw = pw;
        this.email = email;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.expired = false;
    }
}