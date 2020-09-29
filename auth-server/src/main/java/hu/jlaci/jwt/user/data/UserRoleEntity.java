package hu.jlaci.jwt.user.data;

import hu.jlaci.jwt.Role;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserRoleEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role name;

    @ManyToOne
    private UserEntity user;

}
