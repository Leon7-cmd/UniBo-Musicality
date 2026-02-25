package it.unibo.musicality.model;

public class User {
    private String email;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String adminCode;
    private String userType; // 'autore' or 'ascoltatore'

    public User() {}

    public User(String email, String username, String password, String nome, String cognome, String codiceAmministratore, String tipoUtente) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = nome;
        this.surname = cognome;
        this.adminCode = codiceAmministratore;
        this.userType = tipoUtente;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String nome) { this.name = nome; }
    public String getSurname() { return surname; }
    public void setSurname(String cognome) { this.surname = cognome; }
    public String getUserType() { return userType; }
    public void setUserType(String tipoUtente) { this.userType = tipoUtente; }
    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String codiceAmministratore) { this.adminCode = codiceAmministratore; }
}