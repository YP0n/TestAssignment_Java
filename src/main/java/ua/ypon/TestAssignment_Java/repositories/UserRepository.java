package ua.ypon.TestAssignment_Java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ua.ypon.TestAssignment_Java.models.User;

import java.util.Date;
import java.util.List;

/**
 * @author ua.ypon 28.09.2023
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> searchUserByBirth_dateBetweenOrderByBirth_date(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
