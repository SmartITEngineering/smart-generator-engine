/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.engine.webservice.resource;

import com.smartitengineering.util.rest.atom.server.AbstractResource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author saumitra
 */
@Path("/reportconfigs")
public class ReportConfigsResource extends AbstractResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    return responseBuilder.build();
  }

  @Override
  protected String getAuthor() {
    return "Smart Generator";
  }
}
