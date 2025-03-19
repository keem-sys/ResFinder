package accommodationfinder.data;


import accommodationfinder.auth.User;
import accommodationfinder.auth.UserDao;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private final String jdbcUrl = 

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(Long id) {

    }
}
