package BusinessLogic;

import BusinessLogic.validators.ClientNameValidator;
import BusinessLogic.validators.Validator;
import DataAcces.ClientDAO;
import Model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class ClientBLL {

    private List<Validator<Client>> validators;
    private ClientDAO clientDAO;

    public ClientBLL() {
        validators = new ArrayList<>();
        validators.add(new ClientNameValidator());

        clientDAO = new ClientDAO();
    }

    /**
     * Finds a client by its ID.
     *
     * @param id the ID of the client
     * @return the client
     */
    public Client findClientById(int id) {
        Client client = clientDAO.findById(id);
        if (client == null) {
            throw new NoSuchElementException("Clientul cu id = " + id + " nu a fost gasit.");
        }
        return client;
    }

    /**
     * Validates the client using validators.
     *
     * @param client the client to validate
     */
    public void validate(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
    }
}
