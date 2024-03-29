/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.controller;

import com.meteocal.business.exceptions.InvalidInputException;
import com.meteocal.business.exceptions.NotFoundException;
import com.meteocal.business.facade.EventFacade;
import com.meteocal.business.shared.scheduling.ScheduleActivator;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

/**
 *
 * @author Andrea Bignoli
 */
@Singleton
public class ScheduledController {
    
    @EJB
    EventFacade eventFacade;
    
    private static final Logger logger = Logger.getLogger(ScheduledController.class.getName());
    
    @Schedule(second = "*/20", minute = "*", hour = "*", persistent = false)
    public void updateEventsAndForecasts() {
        if (ScheduleActivator.WEATHER_FORECAST_UPDATER) {
            try {
                logger.log(Level.INFO, "[SCHEDULED CONTROLLER] Checking event schedule.");
                eventFacade.checkEventsSchedule();
                
                logger.log(Level.INFO, "[SCHEDULED CONTROLLER] Updating event forecasts and suggested changes.");
                // Update forecasts and check the need for a change suggestion
                eventFacade.updateWeatherForecasts();
            } catch (InvalidInputException ex) {
                Logger.getLogger(ScheduledController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotFoundException ex) {
                Logger.getLogger(ScheduledController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
