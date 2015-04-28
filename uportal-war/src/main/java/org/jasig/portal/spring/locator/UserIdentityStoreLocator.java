/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * 
 */
package org.jasig.portal.spring.locator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.IUserIdentityStore;
import org.jasig.portal.spring.PortalApplicationContextLocator;
import org.springframework.context.ApplicationContext;

/**
 * @author Nicholas Blair, npblair@wisc.edu
 * @deprecated code that needs an {@link IUserIdentityStore} should use direct dependency injection where possible
 */
@Deprecated
public class UserIdentityStoreLocator extends
		AbstractBeanLocator<IUserIdentityStore> {

	public static final String BEAN_NAME = "userIdentityStore";

    private static final Log LOG = LogFactory.getLog(UserIdentityStoreLocator.class);
    private static AbstractBeanLocator<IUserIdentityStore> locatorInstance;
    
    public static IUserIdentityStore getUserIdentityStore() {
    	AbstractBeanLocator<IUserIdentityStore> locator = locatorInstance;
    	if (locator == null) {
            LOG.info("Looking up bean '" + BEAN_NAME + "' in ApplicationContext due to context not yet being initialized");
            final ApplicationContext applicationContext = PortalApplicationContextLocator.getApplicationContext();
            applicationContext.getBean(UserIdentityStoreLocator.class.getName());
            
            locator = locatorInstance;
            if (locator == null) {
                LOG.warn("Instance of '" + BEAN_NAME + "' still null after portal application context has been initialized");
                return applicationContext.getBean(BEAN_NAME, IUserIdentityStore.class);
            }
        }
        
        return locator.getInstance();
    }
	public UserIdentityStoreLocator(IUserIdentityStore instance) {
		super(instance, IUserIdentityStore.class);
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.portal.spring.locator.AbstractBeanLocator#getLocator()
	 */
	@Override
	protected AbstractBeanLocator<IUserIdentityStore> getLocator() {
		return locatorInstance;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jasig.portal.spring.locator.AbstractBeanLocator#setLocator(org.jasig.portal.spring.locator.AbstractBeanLocator)
	 */
	@Override
	protected void setLocator(AbstractBeanLocator<IUserIdentityStore> locator) {
		locatorInstance = locator;
	}

}
