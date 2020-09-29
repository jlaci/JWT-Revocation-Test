package hu.jlaci.jwt.auth.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListEntryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String token;

    public BlackListEntryEntity(String token) {
        this.token = token;
    }
}
