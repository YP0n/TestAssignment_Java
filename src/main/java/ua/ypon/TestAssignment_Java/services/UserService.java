package ua.ypon.TestAssignment_Java.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.ypon.TestAssignment_Java.models.User;
import ua.ypon.TestAssignment_Java.repositories.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author ua.ypon 28.09.2023
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findOne(int id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.orElse(null);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void update(int id, User updatedUser) {
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    public List<User> searchUserByBirth_date(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new IllegalArgumentException("Error: 'From' date should be less than 'To' date.");
        }
        return userRepository.searchUserByBirth_dateBetweenOrderByBirth_date(startDate, endDate);
    }
}
