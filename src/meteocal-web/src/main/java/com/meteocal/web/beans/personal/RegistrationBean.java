/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.web.beans.personal;

import com.meteocal.business.entities.User;
import com.meteocal.business.facade.UserFacade;
import com.meteocal.business.security.UserManager;
import com.meteocal.web.utility.SYSO_Testing;
import com.meteocal.web.utility.SessionUtility;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
/**
 *
 * @author Andrea Bignoli
 */
@ManagedBean
@RequestScoped
public class RegistrationBean implements Serializable {

    @PostConstruct
    public void init() {
        SYSO_Testing.syso("init-RegistrationBean");
        setUser(new User());
    }

    @EJB
    UserManager um;

    @EJB
    UserFacade uf;

    @Inject
    SessionUtility sessionUtility;

    private String passwordConfirmation;
    private User userToRegister;

    /**
     * Creates a new instance of LoginBean
     */
    public RegistrationBean() {
    }

    public String getPasswordConfirmation() {
        return this.passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConf) {
        this.passwordConfirmation = passwordConf;
    }

    public User getUser() {
        return userToRegister;
    }

    public void setUser(User user) {
        this.userToRegister = user;
    }

    private boolean passwordMatching() {
        return userToRegister.getPassword().equals(this.passwordConfirmation);
    }

    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (uf.isUsernameInUse(userToRegister.getUsername())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Username already present!", "Username already present!"));
            return "/Index";
        }

        if (passwordMatching()) {
            SYSO_Testing.clean();
            SYSO_Testing.syso("Starting registration!");
            SYSO_Testing.syso("name: " + userToRegister.getUsername() + " psw: " + userToRegister.getPassword());
            //I've to use a try-catch or a boolean function in order to check if the username is available
            um.register(userToRegister);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration ends successfully!", "Registration ends successfully!"));
            SYSO_Testing.syso("Registration complete!");
        }
        else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Credentials are not valid!", "Credentials are not valid!"));
        }
        return "/Index";
    }

}
