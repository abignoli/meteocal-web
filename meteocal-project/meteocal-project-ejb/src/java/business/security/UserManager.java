/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security;

import business.entities.User;
import business.facade.UserFacade;
import business.shared.data.Group;
import java.security.Principal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Andrea Bignoli
 */
@Stateless
public class UserManager {
    
    @EJB
    UserFacade userFacade;
    
    @Inject
    Principal principal;
    
    public void register(User user) {
        user.setGroupName(Group.USER.getName());
        userFacade.save(user);
    }
    
    public void unregister() {
        userFacade.remove(getLoggedUser());
    }

    public User getLoggedUser() {
        return userFacade.findByUsername(principal.getName());
    }
}