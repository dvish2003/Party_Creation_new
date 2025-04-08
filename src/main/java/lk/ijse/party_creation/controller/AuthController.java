package lk.ijse.party_creation.controller;

import lk.ijse.party_creation.dto.AuthDTO;
import lk.ijse.party_creation.dto.ResponseDTO;
import lk.ijse.party_creation.dto.UserDTO;
import lk.ijse.party_creation.entity.User;
import lk.ijse.party_creation.service.Impl.UserServiceImpl;
import lk.ijse.party_creation.util.JwtUtil;
import lk.ijse.party_creation.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    //constructor injection
    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserServiceImpl userService, ResponseDTO responseDTO) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseDTO> authenticate(@RequestBody UserDTO userDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(VarList.Unauthorized, "Invalid Credentials", e.getMessage()));
        }

        UserDTO loadedUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
        if (loadedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO(VarList.Conflict, "Authorization Failure! Please Try Again", null));
        }

        String token = jwtUtil.generateToken(loadedUser);
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO(VarList.Conflict, "Authorization Failure! Please Try Again", null));
        }

        AuthDTO authDTO = new AuthDTO();
        authDTO.setEmail(loadedUser.getEmail());
        authDTO.setRole(loadedUser.getRole());
        authDTO.setToken(token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(VarList.Created, "Success", authDTO));
    }
}

/*
    @GetMapping
    public ResponseEntity<ResponseDTO> checkRole(@RequestParam String email) {
        User user = userService.findByEmail(email);

        ResponseDTO response = new ResponseDTO();

        if (user != null) {
            if ("Admin".equals(user.getRole())) {
                response.setCode(200);  // Success code
                response.setMessage("User is Admin.");
                response.setData("Admin");
            } else if ("User".equals(user.getRole())) {
                response.setCode(200);  // Success code
                response.setMessage("User is a regular User.");
                response.setData("User");
            } else {
                response.setCode(404);  // Not Found
                response.setMessage("Role not found.");
                response.setData(null);
            }
        } else {
            response.setCode(404);  // Not Found
            response.setMessage("Email not found.");
            response.setData(null);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

*/