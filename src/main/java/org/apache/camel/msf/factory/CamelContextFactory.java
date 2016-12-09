package org.apache.camel.msf.factory;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.ExplicitCamelContextNameStrategy;
import org.apache.camel.msf.route.ManagedRoute;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelContextFactory {
    private static final Logger logger = LoggerFactory.getLogger(CamelContextFactory.class);

    private static final String PROP_INSTANCE_ID = "camelContextId";

    public static CamelContext create(Dictionary dictionary) throws RuntimeException, ConfigurationException {
        logger.info("Camel MSF Updating: " + dictionary);

        String instanceId = (String) dictionary.get(PROP_INSTANCE_ID);
        if (instanceId == null) {
            throw new ConfigurationException(PROP_INSTANCE_ID, "camelContextId must be set");
        }

        try {
            Properties props = new Properties();
            Enumeration<String> e = dictionary.keys();
            while(e.hasMoreElements()) {
                String k = e.nextElement();
                String v = (String) dictionary.get(k);
                System.out.println(k + ": " + v);
                props.put(k, v);
            }

            PropertiesComponent pc = new PropertiesComponent();
            pc.setInitialProperties(props);

            CamelContext camelContext = new DefaultCamelContext();
            camelContext.addComponent("properties", pc);

            camelContext.setNameStrategy(new ExplicitCamelContextNameStrategy(instanceId));
            camelContext.addRoutes(new ManagedRoute());

            logger.info("Camel MSF CamelContext Started: " + camelContext.getName());
            return camelContext;

        } catch (Exception e) {
            throw new ConfigurationException(null, "Cannot start the CamelContext", e);
        }
    }

}
