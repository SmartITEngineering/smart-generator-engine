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

/**
 *
 * @author saumitra
 */
@Path("/reportconfigs/id/{id}")
public class ReportConfigResource extends AbstractResource{

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml(){
    Response.ResponseBuilder responseBuilder = Response.status(Response.Status.OK);
    return responseBuilder.build();
  }
  @Override
  protected String getAuthor() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
