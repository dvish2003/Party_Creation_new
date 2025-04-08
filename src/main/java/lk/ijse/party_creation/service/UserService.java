package lk.ijse.party_creation.service;

import lk.ijse.party_creation.dto.UserDTO;

public interface UserService {
    int saveUser(UserDTO userDTO);

    int verifyUser(String email, String code);

    UserDTO searchUser(String email);
    int updateUser(UserDTO userDTO);
}
