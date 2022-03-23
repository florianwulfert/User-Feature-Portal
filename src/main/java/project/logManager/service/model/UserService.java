package project.logManager.service.model;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import project.logManager.common.dto.LogRequestDto;
import project.logManager.common.dto.UserRequestDto;
import project.logManager.common.message.InfoMessages;
import project.logManager.model.entity.User;
import project.logManager.model.repository.UserRepository;
import project.logManager.service.validation.UserValidationService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
  private final LogService logService;
  private final UserRepository userRepository;
  private final BmiService bmiService;
  private final UserValidationService userValidationService;

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);

  public String addUser(UserRequestDto userRequestDto) {
    userValidationService.checkIfAnyEntriesAreNull(userRequestDto);
    User user =
        User.builder()
            .name(userRequestDto.name)
            .birthdate(userRequestDto.getBirthdateAsLocalDate())
            .weight(userRequestDto.weight)
            .height(userRequestDto.height)
            .favouriteColor(userRequestDto.favouriteColor.toLowerCase())
            .bmi(bmiService.calculateBMI(userRequestDto.weight, userRequestDto.height))
            .build();

    userValidationService.validateFarbenEnum(userRequestDto.favouriteColor.toLowerCase());
    userValidationService.checkIfUserToPostExists(userRequestDto.name);
    if (userValidationService.checkIfUsersListIsEmpty(userRequestDto.actor, user, true)) {
      saveUser(user, userRequestDto.actor);
    } else {
      User activeUser = userValidationService.checkIfNameExists(userRequestDto.actor, true);
      saveUser(user, activeUser.getName());
    }
    return bmiService.calculateBmiAndGetBmiMessage(
        userRequestDto.getBirthdateAsLocalDate(), userRequestDto.weight, userRequestDto.height);
  }

  public List<User> findUserList() {
    return userRepository.findAll();
  }

  public Optional<User> findUserById(Integer id) {
    return userRepository.findById(id).isPresent() ? userRepository.findById(id) : Optional.empty();
  }

  public String deleteById(Integer id, String actorName) {
    User userToDelete = userValidationService.checkIfIdExists(id);
    User actor = userValidationService.checkIfNameExists(actorName, false);
    userValidationService.checkIfUserToDeleteIdEqualsActorId(id, actor.getId());
    userValidationService.checkIfUsersListIsEmpty(actor.getName(), userToDelete, false);
    userValidationService.checkIfExistLogByUserToDelete(userToDelete);

    userRepository.deleteById(id);
    saveLog(String.format(InfoMessages.USER_DELETED_ID, id), "WARNING", actorName);
    LOGGER.info(String.format(InfoMessages.USER_DELETED_ID, id));
    return String.format(InfoMessages.USER_DELETED_ID, id);
  }

  public String deleteByName(String name, String actorName) {
    User user = userValidationService.checkIfNameExists(name, false);
    userValidationService.checkIfExistLogByUserToDelete(user);
    userValidationService.checkIfNameExists(actorName, false);
    userValidationService.checkIfUserToDeleteEqualsActor(name, actorName);

    userRepository.deleteById(user.getId());
    saveLog(String.format(InfoMessages.USER_DELETED_NAME, name), "WARNING", actorName);
    LOGGER.info(String.format(InfoMessages.USER_DELETED_NAME, name));
    return String.format(InfoMessages.USER_DELETED_NAME, name);
  }

  public String deleteAll(String actor) {
    userValidationService.checkIfUsersAreReferenced();
    userRepository.deleteAll();
    LOGGER.info(InfoMessages.ALL_USERS_DELETED);
    saveLog(InfoMessages.ALL_USERS_DELETED,"INFO", actor);
    return InfoMessages.ALL_USERS_DELETED;
  }

  private void saveUser(User user, String actor) {
    userRepository.save(user);
    String bmi =
        bmiService.calculateBmiAndGetBmiMessage(
            user.getBirthdate(), user.getWeight(), user.getHeight());
    saveLog(String.format(InfoMessages.USER_CREATED + "%s", user.getName(), bmi),"INFO", actor);
    LOGGER.info(
        String.format(
            InfoMessages.USER_CREATED
                + bmiService.calculateBmiAndGetBmiMessage(
                    user.getBirthdate(), user.getWeight(), user.getHeight()),
            user.getName()));
  }

  private void saveLog(String message, String severity, String actor) {
    LogRequestDto logRequestDto =
            LogRequestDto.builder()
                    .message(message)
                    .severity(severity)
                    .user(actor)
                    .build();
    LOGGER.info("Log " + logRequestDto + String.format(" was saved as %s", severity));
    logService.addLog(logRequestDto);
  }
}
