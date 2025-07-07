package hr.tvz.java.freelance.freelancemanagementtool.model;

/**
 * Represents a client.
 */
public final class Client extends Entity {
    private String name;
    private String email;
    private String contactPerson;

    /**
     * Client constructor
     *
     * @param id ID value of the client
     * @param name Name of the client
     * @param email Email of the client
     * @param contactPerson Contact Person of the client
     */
    public Client(long id, String name, String email, String contactPerson) {
        super(id);
        this.name = name;
        this.email = email;
        this.contactPerson = contactPerson;
    }

    /**
     * Gets the name of the client
     *
     * @return Name string
     */
    public String getName() { return name; }

    /**
     * Sets the name of the client
     *
     * @param name Name string
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the email of the client
     *
     * @return Email string
     */
    public String getEmail() { return email; }

    /**
     * Sets the email of the client
     *
     * @param email Email string
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the contact person
     *
     * @return Contact person string
     */
    public String getContactPerson() { return contactPerson; }

    /**
     * Sets the contact person
     *
     * @param contactPerson Contact person string
     */
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    /**
     * Overrides the original toString function so it returns it as a readable string
     *
     * @return Readable client string
     */
    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email=" + email + '\'' +
                ", contact person=" + contactPerson +
                '}';
    }
}