package lk.ijse.party_creation.controller;

import lk.ijse.party_creation.dto.AuthDTO;
import lk.ijse.party_creation.dto.ResponseDTO;
import lk.ijse.party_creation.dto.UserDTO;
import lk.ijse.party_creation.service.UserService;
import lk.ijse.party_creation.util.JwtUtil;
import lk.ijse.party_creation.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/getUser")
    public ResponseEntity<ResponseDTO> getUser(@RequestBody UserDTO userDTO) {
        UserDTO userDTO1 = userService.searchUser(userDTO.getEmail());
        System.out.println("ndsfisjdifsn"+userDTO.getUserId());
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User Not Found", null));
        }
        return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Success", userDTO1));
    }


    @PostMapping(value = "/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody  UserDTO userDTO) {
        System.out.println("Register");
        System.out.println(userDTO.getRole());
        System.out.println(userDTO.getName());
        try {
            int res = userService.saveUser(userDTO);
            switch (res) {
                case VarList.Created: {
                    String token = jwtUtil.generateToken(userDTO);
                    AuthDTO authDTO = new AuthDTO();
                    authDTO.setEmail(userDTO.getEmail());
                    authDTO.setRole(userDTO.getRole());
                    authDTO.setToken(token);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Success", authDTO));
                }
                case VarList.Not_Acceptable: {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(VarList.Not_Acceptable, "Email Already Used", null));
                }
                default: {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(VarList.Bad_Gateway, "Error", null));
                }
            }
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();  // You can replace this with a proper logging mechanism
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Internal Server Error: " + e.getMessage(), null));
        }
    }

    @PutMapping(value = "/update")
    public ResponseEntity<ResponseDTO> updateUser(@RequestParam String email, @RequestBody UserDTO userDTO) {
        try{
            // update user email or Password then create new token
            int res = userService.updateUser(userDTO);
            switch (res) {
                case VarList.Created -> {
                    String token = jwtUtil.generateToken(userDTO);
                    AuthDTO authDTO = new AuthDTO();
                    authDTO.setEmail(userDTO.getEmail());
                    authDTO.setToken(token);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Success", authDTO));
                }
                case VarList.Not_Acceptable -> {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(VarList.Not_Acceptable, "Email Already Used", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(VarList.Bad_Gateway, "Error", null));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}