package by.iba.command.authorithation;


import by.iba.command.Command;
import by.iba.command.CommandResult;
import by.iba.command.session.SessionAttribute;
import by.iba.exception.IncorrectDataException;
import by.iba.exception.ServiceException;
import by.iba.model.User;
import by.iba.service.UserService;
import by.iba.util.HashPassword;
import by.iba.util.pages.Page;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.util.Optional;

import static by.iba.command.authorithation.constants.AuthConstants.LOGIN;
import static by.iba.command.authorithation.constants.AuthConstants.PASSWORD;
import static by.iba.command.authorithation.constants.AuthConstants.*;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class LoginCommand implements Command {
    private static final Logger logger = LogManager.getLogger(LoginCommand.class.getName());

    private void setAttributesToSession(String name, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionAttribute.NAME, name);
    }

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) throws ServiceException, IncorrectDataException, IOException {
        boolean isUserFind = false;
        Optional<String> login = of(request).map(httpServletRequest -> httpServletRequest.getParameter(LOGIN));
        Optional<String> password = of(request).map(httpServletRequest -> httpServletRequest.getParameter(PASSWORD));
        System.out.println(login);
        System.out.println(password);
        if (login.isPresent() && password.isPresent()) {
            byte[] pass = HashPassword.getHash(password.get());
            isUserFind = initializeUserIfExist(login.get(), pass, request);
            if (!isUserFind) {
                logger.info("user with such login and password doesn't exist");
                return forwardLoginWithError(request, ERROR_MESSAGE, AUTHENTICATION_ERROR_TEXT);
            } else {
                logger.info("user has been authorized: login:" + login + " password:" + password);
                return new CommandResult(COMMAND_WELCOME, false);
            }
        }else {
            // Handle the case where either login or password is missing
            return forwardLoginWithError(request, ERROR_MESSAGE, ERROR_MESSAGE_TEXT);
        }
    }

    public boolean initializeUserIfExist(String login, byte[] password, HttpServletRequest request) throws ServiceException {
        UserService userService = new UserService();
        Optional<User> user = userService.login(login, password);
        boolean userExist = false;
        if (user.isPresent()) {
            setAttributesToSession(user.get().getLogin(), request);
            userExist = true;
        }
        return userExist;
    }

    private CommandResult forwardLoginWithError(HttpServletRequest request, final String ERROR, final String ERROR_MESSAGE) {
        request.setAttribute(ERROR, ERROR_MESSAGE);
        return new CommandResult(Page.LOGIN_PAGE.getPage(), false);
    }
}

