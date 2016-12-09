package org.apache.camel.msf.factory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelServiceFactory implements ManagedServiceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelServiceFactory.class);
    private static final String PROP_INSTANCE_ID = "camelContextId";

    private BundleContext bundleContext;
    private Map<String, CamelContext> camelContexts = new HashMap<String, CamelContext>();
    private Map<String, ServiceRegistration<CamelContext>> managedRegistrations = new HashMap<String, ServiceRegistration<CamelContext>>();

    public String getName() {
        return "Camel MSF";
    }

    public synchronized void updated(String pid, @SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        LOGGER.info("Camel MSF Updating: " + properties);

        String instanceId = (String) properties.get(PROP_INSTANCE_ID);
        if (instanceId == null) {
            throw new ConfigurationException(PROP_INSTANCE_ID, "Property must be set");
        }

        try {
            deleted(pid);

            CamelContext camelContext = CamelContextFactory.create(properties);
            camelContext.start();

            LOGGER.info("Camel MSF CamelContext Started: " + camelContext.getName());

            Hashtable<String, String> ht = new Hashtable<String, String>();
            ht.put(PROP_INSTANCE_ID, instanceId);
            ServiceRegistration<CamelContext> serviceRegistration = bundleContext.registerService(CamelContext.class, camelContext, ht);

            managedRegistrations.put(pid, serviceRegistration);
            camelContexts.put(pid, camelContext);
        } catch (Exception e) {
            throw new ConfigurationException(null, "Cannot start the CamelContext", e);
        }
    }

    public synchronized void deleted(String pid) {
        ServiceRegistration<CamelContext> serviceRegistration = managedRegistrations.remove(pid);
        if (serviceRegistration != null) {
            try {
                serviceRegistration.unregister();
            } catch (Exception e) {
            }
        }

        CamelContext camelContext = camelContexts.remove(pid);
        if (camelContext != null) {
            try {
                LOGGER.info("Camel MSF Stopping CamelContext: " + camelContext.getName());
                camelContext.stop();
            } catch (Exception e) {
            }
        }
    }

    public synchronized void init() {
        LOGGER.info("Camel MSF Started");
    }

    public synchronized void destroy() {
        /* Create a copy of the keyset before iterating over it to avoid ConcurrentModificationExceptions. */
        Set<String> keys = new HashSet<String>(camelContexts.keySet());
        for (String key: keys) {
            deleted(key);
        }
        LOGGER.info("Camel MSF Stopped");
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
