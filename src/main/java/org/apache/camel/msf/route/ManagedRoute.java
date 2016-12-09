package org.apache.camel.msf.route;

import org.apache.camel.builder.RouteBuilder;

public class ManagedRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("{{from}}")
                .setBody(simple("Message: ${properties:camelContextId:not_found}"))
                .to("{{to}}");
    }
}
