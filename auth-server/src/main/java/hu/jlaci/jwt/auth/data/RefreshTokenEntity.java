package hu.jlaci.jwt.auth.data;

import hu.jlaci.jwt.user.data.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant validUntil;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

}
