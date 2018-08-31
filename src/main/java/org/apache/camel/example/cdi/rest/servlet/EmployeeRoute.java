package org.apache.camel.example.cdi.rest.servlet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

@ApplicationScoped
@ContextName("employee")
public class EmployeeRoute extends RouteBuilder {

	public static final String APPLICATION_JSON = "application/json";
	public static final String TEXT_PLAIN = "text/plain";

	@Inject
	MongoBean mongoBean;

	@Override
	public void configure() throws Exception {

		rest("/employee").get("/insert/{id}").produces(TEXT_PLAIN)
				.to("direct:insert")

				.get("/getById/{id}").produces(APPLICATION_JSON)
				.to("direct:getbyId")

				.get("/getAll").produces(APPLICATION_JSON).to("direct:getAll");

		from("direct:insert").bean(mongoBean,
				"insertEmployee(${header.id},${header.name})").log("${body}");

		from("direct:getbyId")
				.bean(mongoBean, "findEmployeeById(${header.id})").log("${body}");

		from("direct:getAll").bean(mongoBean, "findAllEmployees()").log("${body}");

	}

}
