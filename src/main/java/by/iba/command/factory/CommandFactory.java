package by.iba.command.factory;


import by.iba.command.Command;
import by.iba.command.LoginPageCommand;
import by.iba.command.RegisterPageCommand;
import by.iba.command.authorithation.LoginCommand;
import by.iba.command.authorithation.RegisterNewUserCommand;
import by.iba.command.authorithation.SignOutCommand;
import by.iba.command.grouppersons.AddNewPersonCommand;
import by.iba.command.grouppersons.WelcomeCommand;

//Создает команды
public class CommandFactory {
    public static Command create(String command) {
        command = command.toUpperCase();
        System.out.println(command);
        CommandType commandEnum = CommandType.valueOf(command);
        Command resultCommand;
        switch (commandEnum) {
            case LOGIN: {
                resultCommand = new LoginCommand();
                break;
            }
            case REGISTER_NEW_USER: {
                resultCommand = new RegisterNewUserCommand();
                break;
            }
            case SIGN_OUT: {
                resultCommand = new SignOutCommand();
                break;
            }
            case ADD_NEW_PERSON: {
                resultCommand = new AddNewPersonCommand();
                break;
            }
            case LOGIN_PAGE: {
                resultCommand = new LoginPageCommand();
                break;
            }
            case WELCOME: {
                resultCommand = new WelcomeCommand();
                break;
            }
            case REGISTRATION_PAGE: {
                resultCommand = new RegisterPageCommand();
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid command" + commandEnum);
            }
        }
        return resultCommand;
    }
}

