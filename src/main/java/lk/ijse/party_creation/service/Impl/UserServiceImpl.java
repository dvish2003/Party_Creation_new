package lk.ijse.party_creation.service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.party_creation.config.VerificationCodeGenerator;
import lk.ijse.party_creation.dto.UserDTO;
import lk.ijse.party_creation.entity.User;
import lk.ijse.party_creation.repo.UserRepo;
import lk.ijse.party_creation.service.UserService;
import lk.ijse.party_creation.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;


    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;  // Email already exists
        } else {
            // Hash the password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(encodedPassword);  // Set the encoded password
            String verificationCode = VerificationCodeGenerator.generateCode(6);


            // Save the user after mapping to the User entity
            User user = modelMapper.map(userDTO, User.class);
            user.setVerificationCode(verificationCode);
            user.setVerified(false);
            userRepo.save(user);
            emailService.sendVerificationEmail(user.getEmail(),user.getVerificationCode());

            return VarList.Created;  // User successfully created
        }
    }

    @Override
    public int verifyUser(String email, String code) {
        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            return VarList.Bad_Request;
        }

        try {

            User user = userRepo.findByEmail(email);
            if (user == null) {
                return VarList.Not_Found;
            }
            if (user.isVerified()) {
                return VarList.Conflict;
            }
            if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
                return VarList.Unauthorized;
            }
            user.setVerificationCode(null);
            user.setVerified(true);
            userRepo.save(user);

            return VarList.OK;

        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public UserDTO searchUser(String email) {
        if (userRepo.existsByEmail(email)) {
            User user = userRepo.findByEmail(email);
            return modelMapper.map(user, UserDTO.class);
        } else {
            return null;  // Return null if user doesn't exist
        }
    }

    @Override
    public int updateUser(UserDTO userDTO) {
      /*  try {
            User user = userRepo.findByEmail(userDTO.getEmail());
            if (user!= null) {
                if (userDTO.getPassword()!= null) {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
                    user.setPassword(encodedPassword);
                }

                userRepo.save(modelMapper.map(userDTO, User.class));
                return VarList.No_Content;
            } else {
                return VarList.NotFound;
            }
        }catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }*/
        return 0;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthority(user));
    }

    public UserDTO loadUserDetailsByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
