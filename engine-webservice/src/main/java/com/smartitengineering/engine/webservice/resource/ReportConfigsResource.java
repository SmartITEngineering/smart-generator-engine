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
import com.sun.jersey.api.view.Viewable;
import java.util.Collection;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author saumitra
 */
@Path("/reportconfigs")
public class ReportConfigsResource extends AbstractResource {

  @Context
  private HttpServletRequest servletRequest;
  private GenericAdapterImpl<ReportConfig, PersistentReportConfig> adapter;

  @DefaultValue("10")
  @QueryParam("count")
  private Integer count;

  public ReportConfigsResource() {
    adapter = new GenericAdapterImpl<ReportConfig, PersistentReportConfig>();
    //adapter.setHelper(new ReportConfigAdapterHelper());
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    Feed atomFeed = getFeed("ReportConfigs", new Date());
    Collection<PersistentReportConfig> reportConfigs = Services.getInstance().getReportConfigService().getAll();
    if (reportConfigs != null && !reportConfigs.isEmpty()) {
      for (PersistentReportConfig reportConfig : reportConfigs) {
        Entry reportConfigEntry = getAbderaFactory().newEntry();
        reportConfigEntry.setId(reportConfig.getId());
        reportConfigEntry.setTitle(reportConfig.getName());

        Link reportConfigLink = getAbderaFactory().newLink();
        reportConfigLink.setHref(getRelativeURIBuilder().path(ReportConfigResource.class).build(reportConfig.getId()).toString());
        reportConfigLink.setRel(Link.REL_ALTERNATE);
        reportConfigLink.setMimeType(MediaType.APPLICATION_ATOM_XML);

        reportConfigEntry.addLink(reportConfigLink);
        atomFeed.addEntry(reportConfigEntry);
      }
    }
    return responseBuilder.build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("/{index}")
  public Response getHtmlIndex(@PathParam("index") Integer index){
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    if (count == null) {
      count = 10;
    }
    Collection<PersistentReportConfig> configs = Services.getInstance().getReportConfigService().getAll();

    servletRequest.setAttribute("index", index);
    servletRequest.setAttribute("total", Services.getInstance().getReportConfigService().getAll().size());
    servletRequest.setAttribute("count", count);
    Viewable view = new Viewable("",adapter.convertInversely(configs.toArray(new PersistentReportConfig[configs.size()])));

    responseBuilder.entity(view);
    return responseBuilder.build();

  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    return responseBuilder.build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response post(ReportConfig reportConfig) {
    ResponseBuilder responseBuilder = Response.status(Status.CREATED);
    PersistentReportConfig persistentReportConfig = adapter.convert(reportConfig);
    try {
      basicPost(persistentReportConfig);
    }
    catch (Exception ex) {
      servletRequest.setAttribute("error", ex.getMessage());
    }

    return responseBuilder.build();

  }

  public void basicPost(PersistentReportConfig reportConfig) {
    Services.getInstance().getReportConfigService().save(reportConfig);
  }

  @Override
  protected String getAuthor() {
    return "Smart Generator";
  }
}
