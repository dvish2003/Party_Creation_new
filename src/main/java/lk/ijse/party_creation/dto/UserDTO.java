package lk.ijse.party_creation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private UUID userId;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean verified;
    private String verificationCode;

/*    private boolean verified;
    private String verificationCode;*/
}
