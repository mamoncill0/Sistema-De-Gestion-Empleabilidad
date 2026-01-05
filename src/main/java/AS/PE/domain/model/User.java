package AS.PE.domain.model;

import AS.PE.domain.model.enums.Role;

public class User {
    private Long idUser;
    private String username;
    private String email;
    private String password;
    private Role role;

    public User() {
    }

    public User(Long idUser, String username, String email, String password, Role role) {
        this.idUser = idUser;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}