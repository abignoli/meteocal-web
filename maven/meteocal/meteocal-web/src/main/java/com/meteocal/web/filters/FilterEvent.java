/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.web.filters;

import com.meteocal.business.entities.User;
import com.meteocal.business.security.UserManager;
import com.meteocal.business.shared.security.UserEventVisibility;
import static com.meteocal.business.shared.security.UserEventVisibility.CREATOR;
import static com.meteocal.business.shared.security.UserEventVisibility.VIEWER;
import com.meteocal.web.utility.SYSO_Testing;
import com.meteocal.web.utility.SessionUtility;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Leo
 */
@ManagedBean
@RequestScoped
public class FilterEvent {

    @EJB
    private UserManager um;
    
    
    @Inject
    private SessionUtility sessionUtility;

    private User loggedUser;
    
    HttpServletRequest request =  (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        
    private final String context = request.getContextPath();
    private final String indexPath = context + "/Index.xhtml";
    private final String creatorPath = context + "/WEB-INF/HiddenPages/EventPageCreator.xhtml";
    private final String viewerPath = context + "/WEB-INF/HiddenPages/EventPageViewer.xhtml";
    private final String noVisibilityPath = context + "/WEB-INF/HiddenPages/EventPageNoVisibility.xhtml";

    @PostConstruct
    public void init() {
        this.setUser(um.getLoggedUser());
        request =  (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        
    }

    private void setUser(User user) {
        this.loggedUser = user;
    }

    public void check(ComponentSystemEvent event) {
        //eventID = SessionUtility.getQueryString().split("=")[1];
        int eventID = 1;
        boolean comingFromRedirect;
        UserEventVisibility visibility;//one of: CREATOR, VIEWER, NO_VISIBILITY

        SYSO_Testing.syso("in EventPage: check");
        String relativePath = request.getRequestURI().replace("/meteocal-web", "");

        SYSO_Testing.syso("URI: " + request.getRequestURI());
        SYSO_Testing.syso("relativePath: " + relativePath);

        try {

            if (isNotLogged()) {
                SYSO_Testing.syso("I'm not logged");
                response.sendRedirect(indexPath);
                SYSO_Testing.syso("something wrong!!");
            }
            else {
                String username = loggedUser.getUsername();
//                try {
//                    visibility = um.getVisibilityOverEvent(eventID);
//                }
//                catch (NotFoundException ex) {
//                    Logger.getLogger(FilterEvent.class.getName()).log(Level.SEVERE, null, ex);
//                    SessionUtility.redirect("/Index.xhtml");
//                }
                visibility = CREATOR;
                SYSO_Testing.syso("Username " + username);
                SYSO_Testing.syso("I'm logged, and I've to check the visibility");
                if (visibility == CREATOR) {
                    SYSO_Testing.syso("creator");
                    sessionUtility.setComingFromDispatcher();
                    sessionUtility.setEventID(eventID);
                    try {
                        SYSO_Testing.syso("pre-dispatcher");
                        request.getRequestDispatcher(creatorPath).forward(request, response);
                        SYSO_Testing.syso("post-dispatcher");
                    }
                    catch (IOException ex) {
                        SYSO_Testing.syso("secondTryCatch");
                        Logger.getLogger(FilterEvent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
                    if (visibility == VIEWER) {
                        SYSO_Testing.syso("viewer");
                        sessionUtility.setComingFromDispatcher();
                        request.getRequestDispatcher(viewerPath).forward(request, response);
                    }
                    else {// NO VISIBILITY
                        SYSO_Testing.syso("no Visibility");
                        sessionUtility.setComingFromDispatcher();
                        request.getRequestDispatcher(noVisibilityPath).forward(request, response);  
                    }
                } 
            }
        }
        catch (NullPointerException ec) {
            Logger.getLogger(FilterEvent.class.getName()).log(Level.SEVERE, null, ec);
        }
        catch (IOException ex) {
            Logger.getLogger(FilterEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ServletException ex) {
            Logger.getLogger(FilterEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isNotLogged() {
        return !sessionUtility.getLoggedUser().equals(this.loggedUser.getUsername());
    }
}