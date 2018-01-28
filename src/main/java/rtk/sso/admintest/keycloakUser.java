/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.sso.admintest;

import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vasil
 *
 */
@XmlRootElement
public class keycloakUser {
    
    private String username;
    private boolean enabled;
    private String email;
    private String firstName;
    private String lastName;

    private HashMap<String, String> attributes;
    private List<credentialRepresentation> credentials;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<credentialRepresentation> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<credentialRepresentation> credentials) {
        this.credentials = credentials;
    }

    @Override
    public String toString() {
        return "keycloakUser{" + "username=" + username + ", enabled=" + enabled + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + ", attributes=" + attributes + ", credentials=" + credentials + '}';
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

}
