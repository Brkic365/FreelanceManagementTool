// File: model/Client.java
package hr.tvz.java.freelance.freelancemanagementtool.model;

/**
 * Represents a client.
 */
public final class Client extends Entity {
    private String name;
    private String email;
    private String contactPerson;

    public Client(long id, String name, String email, String contactPerson) {
        super(id);
        this.name = name;
        this.email = email;
        this.contactPerson = contactPerson;
    }

    // Getters and Setters...
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    @Override
    public String toString() {
        return name; // Useful for ComboBox display
    }
}