package lk.ijse.party_creation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    private String name;
    private String email;
    private String password;
    private String role;
    private boolean verified;
    private String verificationCode;

}
