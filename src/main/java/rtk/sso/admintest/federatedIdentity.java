/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.sso.admintest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vasil
 */
//{"identityProvider":"test_oidc","userId":"24369344","userName":"user_001"} 
@XmlRootElement
public class federatedIdentity {

    private String identityProvider;
    private String userId;
    private String userName;

    public federatedIdentity() {
    }

    public String getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(String identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "federatedIdentity{" + "identityProvider=" + identityProvider + ", userId=" + userId + ", userName=" + userName + '}';
    }

}
