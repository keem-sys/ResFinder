package accommodationfinder.auth;

import java.util.List;

public interface UserDao {
    User getUserById(Long id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
}
