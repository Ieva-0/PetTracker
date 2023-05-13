package edu.ktu.pettrackerclient.users;

import java.util.ArrayList;
import java.util.List;

public class UsersWithRolesResponse {
    List<Role> roles;
    List<User> users;
    public UsersWithRolesResponse() {
        roles = new ArrayList<>();
        users = new ArrayList<>();
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UsersWithRolesResponse{" +
                "roles=" + roles +
                ", users=" + users +
                '}';
    }
}
