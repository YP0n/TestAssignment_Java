package ua.ypon.TestAssignment_Java.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ua.ypon.TestAssignment_Java.models.User;
import ua.ypon.TestAssignment_Java.services.UserService;
import ua.ypon.TestAssignment_Java.util.UserErrorResponse;
import ua.ypon.TestAssignment_Java.util.UserNotCreatedException;
import ua.ypon.TestAssignment_Java.util.UserNotFoundException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author ua.ypon 28.09.2023
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Value("${app.minAgeToRegister}")
    private int minAgeToRegister;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userService.findOne(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid User user,
                                             BindingResult bindingResult
                                    /*@RequestParam(name = "birth_date") @DateTimeFormat(pattern = "yyyy-MM-dd")*/) {
        Date birth_date = user.getBirth_date();
        if(birth_date == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Birth date is required");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(birth_date);
        int yearOfBirth = cal.get(Calendar.YEAR);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - yearOfBirth;

        if (age < minAgeToRegister) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User should be at least" + minAgeToRegister + " years old.");
        }
        return ResponseEntity.ok("User created successfully");
    }

    @PatchMapping
    public ResponseEntity<HttpStatus> update(@Valid @RequestBody User user,
                                             BindingResult bindingResult, @PathVariable("id") int id) {
        userService.update(id, user);

        return getHttpStatusResponseEntity(user, bindingResult);
    }


    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@RequestBody int id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private ResponseEntity<HttpStatus> getHttpStatusResponseEntity(@RequestBody @Valid User user, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new UserNotCreatedException(errorMsg.toString());
        }
        userService.save(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserByBirthDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<User> users = userService.searchUserByBirth_date(startDate, endDate);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                "User with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
