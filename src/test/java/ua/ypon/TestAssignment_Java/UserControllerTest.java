package ua.ypon.TestAssignment_Java;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import ua.ypon.TestAssignment_Java.controllers.UserController;
import ua.ypon.TestAssignment_Java.models.User;
import ua.ypon.TestAssignment_Java.services.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ua.ypon 28.09.2023
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Test
    public void testCreateUserWithValidAge() {

        User user = new User("test@example.com", "Name", "LastName", new Date(), "Address", "123456789");

        ResponseEntity<?> responseEntity = userController.create(user, mock(BindingResult.class));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testCreateUserWithInvalidAge() {
        User user = new User("test@example.com", "Name", "LastName", null, "Address", "123456789");

        ResponseEntity<?> responseEntity = userController.create(user, mock(BindingResult.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

        @Test
        public void testFindAllUsers() {
            UserService userService = mock(UserService.class);

            List<User> fakeUsers = new ArrayList<>();
            fakeUsers.add(new User("test1@example.com", "TestName1", "TestLastName1", new Date(), "Address1", "1234567891"));
            fakeUsers.add(new User("test2@example.com", "TestName2", "TestLastName2", new Date(), "Address2", "1234567892"));

            when(userService.findAll()).thenReturn(fakeUsers);

            UserController userController = new UserController(userService);

            List<User> users = userController.getUsers();

            assertEquals(2, users.size());
        }

        @Test
        public void testUpdateUser() {
            UserService userService = mock(UserService.class);

            User user = new User("test@example.com", "Name", "LastName", new Date(), "Address", "123456789");
            when(userService.findOne(user.getId())).thenReturn(user);

            UserController userController = new UserController(userService);

            user.setFirst_name("New name");
            user.setLast_name("New last name");
            user.setAddress("New address");

            BindingResult bindingResult = new BeanPropertyBindingResult(user, "user");

            ResponseEntity<HttpStatus> responseEntity = userController.update(user, bindingResult, user.getId());

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            User updatedUser = userService.findOne(user.getId());
            assertNotNull(updatedUser);
            assertEquals("New name", updatedUser.getFirst_name());
            assertEquals("New last name", updatedUser.getLast_name());
            assertEquals("New address", updatedUser.getAddress());
        }

        @Test
        public void testDeleteUser() {
            UserService userService = mock(UserService.class);

            User user = new User("test@example.com", "TestName", "TestLastName", new Date(), "Address", "123456789");
            when(userService.findOne(user.getId())).thenReturn(user);

            doNothing().when(userService).delete(user.getId());

            UserController userController = new UserController(userService);
            ResponseEntity<HttpStatus> responseEntity = userController.delete(user.getId());

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

            when(userService.findOne(user.getId())).thenReturn(null);
            User deletedUser = userService.findOne(user.getId());
            assertNull(deletedUser);
        }

    @Test
    public void testSearchUsersByBirthDateRange() {
        UserService userService = mock(UserService.class);

        List<User> fakeUsers = new ArrayList<>();
        fakeUsers.add(new User("test1@example.com", "TestName1", "TestLastName1", new Date(2000, 1, 1), "Address1", "1234567891"));
        fakeUsers.add(new User("test2@example.com", "TestName2", "TestLastName2", new Date(1995, 5, 5), "Address2", "1234567892"));

        Date startDate = new Date(1990, 1, 1);
        Date endDate = new Date(2000, 12, 31);
        when(userService.searchUserByBirth_date(startDate, endDate)).thenReturn(fakeUsers);

        UserController userController = new UserController(userService);

        ResponseEntity<List<User>> responseEntity = userController.searchUserByBirthDateRange(startDate, endDate);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<User> foundUsers = responseEntity.getBody();
        assertEquals(2, foundUsers.size());
    }
}
