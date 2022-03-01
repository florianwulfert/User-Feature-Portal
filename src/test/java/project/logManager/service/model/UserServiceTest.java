package project.logManager.service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.logManager.model.entity.User;
import project.logManager.model.repository.UserRepository;
import project.logManager.service.validation.UserValidationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks UserService systemUnderTest;

  @Mock UserRepository userRepository;

  @Mock BmiService bmiService;

  @Mock UserValidationService userValidationService;

  @Mock LogService logService;

  List<User> users;

  @BeforeEach
  void init() {
    users = addTestUser();
  }

  // TODO: Fix UserService-Tests

  /*@Test
  void testAddUser() {
    Mockito.when(
            bmiService.calculateBmiAndGetBmiMessage(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn("User has a BMI of 24.07 and therewith he has normal weight.");
    Mockito.when(
            userValidationService.checkIfUsersListIsEmpty(
                Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
        .thenReturn(false);
    Mockito.when(userValidationService.checkIfNameExists(Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(users.get(1));
    systemUnderTest.addUser(
        users.get(0).getName(), "Florian", LocalDate.of(1988, 12, 12), 90.0, 1.85, "YELLOW");
    Mockito.verify(logService)
        .addLog(
            String.format(InfoMessages.USER_CREATED + InfoMessages.BMI_MESSAGE, "Florian", 24.07)
                + InfoMessages.NORMAL_WEIGHT,
            "INFO",
            "Florian");
    Mockito.verify(userRepository).save(Mockito.any());
  }

  @Test
  void testUsersListIsEmpty() {
    Mockito.when(
            bmiService.calculateBmiAndGetBmiMessage(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn("User has a BMI of 24.07 and therewith he has normal weight.");
    Mockito.when(
            userValidationService.checkIfUsersListIsEmpty(
                Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
        .thenReturn(true);
    systemUnderTest.addUser("Florian", "Florian", LocalDate.of(2000, 11, 18), 80, 1.85, "blau");
    Mockito.verify(logService)
        .addLog(
            String.format(InfoMessages.USER_CREATED + InfoMessages.BMI_MESSAGE, "Florian", 24.07)
                + InfoMessages.NORMAL_WEIGHT,
            "INFO",
            "Florian");
    Mockito.verify(userRepository).save(Mockito.any());
  }

  @Test
  void testFindUserList() {
    systemUnderTest.findUserList();
    Mockito.verify(userRepository).findAll();
  }

  @Test
  void testFindUserById() {
    systemUnderTest.findUserById(1);
    Mockito.verify(userRepository).findById(1);
  }

  @Test
  void testFindUserByName() {
    userRepository.findUserByName(Mockito.anyString());
    Mockito.verify(userRepository).findUserByName(Mockito.anyString());
  }

  @Test
  void testDeleteById() {
    Mockito.when(userValidationService.checkIfNameExists(Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(users.get(1));
    Assertions.assertEquals(
        String.format(InfoMessages.USER_DELETED_ID, 1),
        systemUnderTest.deleteById(1, users.get(0).getName()));
    Mockito.verify(logService)
        .addLog(String.format(InfoMessages.USER_DELETED_ID, 1), "WARNING", "Peter");
  }

  @Test
  void testDeleteByName() {
    Mockito.when(userValidationService.checkIfNameExists(Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(users.get(1));
    Assertions.assertEquals(
        String.format(InfoMessages.USER_DELETED_NAME, "Florian"),
        systemUnderTest.deleteByName("Florian", "Peter"));
    Mockito.verify(logService)
        .addLog(String.format(InfoMessages.USER_DELETED_NAME, "Florian"), "WARNING", "Peter");
  }

  @Test
  void testDeleteAll() {
    Assertions.assertEquals(InfoMessages.ALL_USERS_DELETED, systemUnderTest.deleteAll());
    Mockito.verify(userRepository).deleteAll();
  }*/

  private List<User> addTestUser() {
    List<User> users = new ArrayList<>();
    users.add(
        User.builder()
            .id(1)
            .name("Peter")
            .birthdate(LocalDate.of(2005, 12, 12))
            .weight(90.0)
            .height(1.85)
            .favouriteColor("yellow")
            .bmi(26.29)
            .build());

    users.add(
        User.builder()
            .id(2)
            .name("Florian")
            .birthdate(LocalDate.of(1988, 12, 12))
            .weight(70.0)
            .height(1.85)
            .favouriteColor("yellow")
            .bmi(20.45)
            .build());
    return users;
  }
}
