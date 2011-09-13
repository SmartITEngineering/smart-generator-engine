/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smartitengineering.engine.webservice.resource;

import com.smartitengineering.engine.domain.PersistentReportConfig;
import com.smartitengineering.engine.domain.ReportConfig;
import com.smartitengineering.engine.service.factory.Services;
import com.smartitengineering.engine.webservice.adapter.ReportConfigAdapterHelper;
import com.smartitengineering.util.bean.adapter.GenericAdapterImpl;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author saumitra
 */
@Path("/reportconfigs/id/{id}")
public class ReportConfigResource extends AbstractResource{

  @PathParam("id")
  private Long id;
  @Context
  private HttpServletRequest servletRequest;
  private GenericAdapterImpl<ReportConfig, PersistentReportConfig> adapter;
  private PersistentReportConfig persistentReportConfig;

  public ReportConfigResource(@PathParam("id") Long id) {
    persistentReportConfig = Services.getInstance().getReportConfigService().getById(id);
    adapter = new GenericAdapterImpl<ReportConfig, PersistentReportConfig>();
    adapter.setHelper(new ReportConfigAdapterHelper());
  }


  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml(){
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    return responseBuilder.build();
  }
  @POST
  @Path("/delete")
  public Response deletePost(ReportConfig reportConfig){
    PersistentReportConfig persistentReportConfig = adapter.convert(reportConfig);
    try{
      Services.getInstance().getReportConfigService().delete(persistentReportConfig);
    }
    catch(Exception ex){
      servletRequest.setAttribute("error", ex);
    }
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    return responseBuilder.build();
  }
  @DELETE
  public Response delete (){
    Services.getInstance().getReportConfigService().delete(persistentReportConfig);
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    return responseBuilder.build();
 
  }
  @Override
  protected String getAuthor() {
    return "Smart Generator";
  }

}
