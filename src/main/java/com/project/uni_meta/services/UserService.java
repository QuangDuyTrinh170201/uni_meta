package com.project.uni_meta.services;

import com.project.uni_meta.components.JwtTokenUtil;
import com.project.uni_meta.dtos.UpdateUserDTO;
import com.project.uni_meta.dtos.UserDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.exceptions.PermissionDenyException;
import com.project.uni_meta.models.Faculty;
import com.project.uni_meta.models.Role;
import com.project.uni_meta.models.User;
import com.project.uni_meta.repositories.FacultyRepository;
import com.project.uni_meta.repositories.RoleRepository;
import com.project.uni_meta.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final FacultyRepository facultyRepository;

    private final AuthenticationManager authenticationManager;
    @Override
    public User CreateUser(UserDTO userDTO) throws DataNotFoundException, PermissionDenyException {
        String username = userDTO.getUserName();
        //check exist username
        if(userRepository.existsByUsername(username)){
            throw new DataIntegrityViolationException("Username already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Not Found"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("You cannot register an admin account!");
        }
        User newUser = User.builder()
                .username(userDTO.getUserName())
                .password(userDTO.getPassword())
                .active(true)
                .build();

        if(userDTO.getFacultyId() != null){
            Faculty faculty = facultyRepository.findById(userDTO.getFacultyId())
                    .orElseThrow(() -> new DataNotFoundException("Faculty Not Found"));
            newUser.setFaculty(faculty);
        }
        newUser.setRole(role);
        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Override
    public Map<String, Object> Login(String username, String password) throws DataNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Username or password is invalid");
        }
        User existingUser = optionalUser.get();
        //check password
        if(!passwordEncoder.matches(password, existingUser.getPassword())){
            throw new BadCredentialsException("Wrong username or password!");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password,
                existingUser.getAuthorities()
        );
        //authenticate with java spring security
        authenticationManager.authenticate(authenticationToken);

        // Prepare the response map
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtTokenUtil.generateToken(existingUser));
        response.put("roleId", existingUser.getRole().getId());
        response.put("roleName", existingUser.getRole().getName()); // Assuming Role is a field in User entity
        if(existingUser.getRole().getId() != 5){
            response.put("facultyId", existingUser.getFaculty().getId());
            response.put("facultyName", existingUser.getFaculty().getName());
        }
        return response;
    }

    @Override
    public List<User> getAllUsers() throws Exception {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new DataNotFoundException("No users found");
            }
            return users;
        } catch (Exception e) {
            throw new Exception("Failed to retrieve users: " + e.getMessage());
        }
    }

    @Override
    public User updateUserInforByAdmin(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Tìm người dùng hiện có dựa vào userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Cập nhật trường isActive nếu có
        Boolean isActive = updatedUserDTO.getIsActive();
        if (isActive != null) {
            existingUser.setActive(isActive);
        }

        // Cập nhật trường roleId nếu có
        Long roleId = updatedUserDTO.getRoleId();
        if (roleId != null) {
            // Truy vấn cơ sở dữ liệu để lấy đối tượng Role tương ứng với roleId
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new DataNotFoundException("Role not found"));

            // Gán đối tượng Role vào trường role của User
            existingUser.setRole(role);
        }

        Long facultyId = updatedUserDTO.getFacultyId();
        if(facultyId != null){
            Faculty faculty = facultyRepository.findById(facultyId)
                    .orElseThrow(() -> new DataNotFoundException("Faculty not found"));
            existingUser.setFaculty(faculty);
        }

        return userRepository.save(existingUser);
    }
}
