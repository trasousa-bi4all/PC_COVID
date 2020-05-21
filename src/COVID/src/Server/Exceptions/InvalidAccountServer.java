package COVID.src.Server.Exceptions;

import COVID.src.Exceptions.AccountException;
import COVID.src.Exceptions.AccountExceptions.InvalidAccount;

public class InvalidAccountServer extends InvalidAccount {
    public InvalidAccountServer(String id) {
        super("A conta " + id + "já existe");
    }
}
