package com.seblong.wp.listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class ContainerContextClosedHandler implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// nothing to do
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		Enumeration<Driver> drivers = DriverManager.getDrivers();

		Driver driver = null;
		// clear drivers
		try {
			while (drivers.hasMoreElements()) {

				driver = drivers.nextElement();
				DriverManager.deregisterDriver(driver);
			}
			// MySQL driver leaves around a thread. This static method cleans it up.
			AbandonedConnectionCleanupThread.checkedShutdown();
//			SystemDate.shutdown();
		} catch (SQLException ex) {
			// deregistration failed, might want to do something, log at the
			// very least
		} 
		
	}

}