package lk.ijse.party_creation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: vishmee
 * Date: 4/8/25
 * Time: 1:27 AM
 * Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyUserDTO {
    private String email;
    private String code;
}
