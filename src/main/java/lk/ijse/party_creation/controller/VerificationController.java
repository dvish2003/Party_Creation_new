package lk.ijse.party_creation.controller;


import lk.ijse.party_creation.dto.ResponseDTO;
import lk.ijse.party_creation.dto.VerifyUserDTO;
import lk.ijse.party_creation.service.UserService;
import lk.ijse.party_creation.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: vishmee
 * Date: 3/18/25
 * Time: 10:45 PM
 * Description:
 */

@RestController
@RequestMapping("/api/v1/verifyUser")
public class VerificationController {
    private final UserService userService;

    public VerificationController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/verify")
    public ResponseEntity<lk.ijse.party_creation.dto.ResponseDTO> verifyUser(@RequestBody VerifyUserDTO verifyUserDTO) {
        System.out.println("email    wwwwwwwwwwwwwwww " + verifyUserDTO.getEmail());
        System.out.println("code   wwwwwwwwwwww  " + verifyUserDTO.getCode());
        try {
            int res = userService.verifyUser(verifyUserDTO.getEmail(), verifyUserDTO.getCode());
            switch (res) {
                case VarList.OK:
                    return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Verification Successful", verifyUserDTO));
                case VarList.Not_Found:
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(VarList.Not_Found, "User Not Found", null));
                default:
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseDTO(VarList.Not_Acceptable, "Invalid Verification Code", null));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
