package Model;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class Client {
    private int id;
    private String name;

    public Client(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}