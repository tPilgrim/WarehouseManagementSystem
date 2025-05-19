package BusinessLogic.validators;

import Model.Client;

public class ClientNameValidator implements Validator<Client> {

    @Override
    public void validate(Client client) {
        String name = client.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Numele clientului este gol.");
        }
        if (!name.matches("[A-Za-z ]+")) {
            throw new IllegalArgumentException("Numaele trbuie sa contina doar litere si spatii.");
        }
    }
}

