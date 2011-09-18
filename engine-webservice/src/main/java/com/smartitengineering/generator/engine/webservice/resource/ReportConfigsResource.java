/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.resource;


import com.smartitengineering.generator.engine.service.ReportConfigFilter;
import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generator.engine.service.factory.Services;
import com.smartitengineering.util.bean.adapter.GenericAdapterImpl;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
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
  private GenericAdapterImpl<ReportConfig, com.smartitengineering.generator.engine.domain.ReportConfig> adapter;

  @DefaultValue("10")
  @QueryParam("count")
  private Integer count;

  public ReportConfigsResource() {
    adapter = new GenericAdapterImpl<ReportConfig, com.smartitengineering.generator.engine.domain.ReportConfig>();
    //adapter.setHelper(new ReportConfigAdapterHelper());
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get(Integer index , @PathParam("startDate") Date startDate , @PathParam("endDate") Date endDate , @PathParam("nameLike") String nameLike) {
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    Feed atomFeed = getFeed("ReportConfigs", new Date());
    ReportConfigFilter reportConfigFilter = new ReportConfigFilter();
    reportConfigFilter.setNameLike(nameLike);
    reportConfigFilter.setScheduleRangeStart(startDate);
    reportConfigFilter.setScheduleRangeEnd(endDate);
    Collection<com.smartitengineering.generator.engine.domain.ReportConfig> reportConfigs = Services.getInstance().getReportConfigService().searchConfigs(reportConfigFilter);
    if (reportConfigs != null && !reportConfigs.isEmpty()) {
      for (com.smartitengineering.generator.engine.domain.ReportConfig reportConfig : reportConfigs) {
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

  public Response getHtmlIndex(@PathParam("index") Integer index , @PathParam("startDate") Date startDate , @PathParam("endDate") Date endDate , @PathParam("nameLike") String nameLike){
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    if (count == null) {
      count = 10;
    }
    ReportConfigFilter reportConfigFilter = new ReportConfigFilter();
    reportConfigFilter.setNameLike(nameLike);
    reportConfigFilter.setScheduleRangeStart(startDate);
    reportConfigFilter.setScheduleRangeEnd(endDate);
    Collection<com.smartitengineering.generator.engine.domain.ReportConfig> configs = Services.getInstance().getReportConfigService().searchConfigs(reportConfigFilter);
    servletRequest.setAttribute("index", index);
    servletRequest.setAttribute("total", Services.getInstance().getReportConfigService().searchConfigs(
        reportConfigFilter).size());
    servletRequest.setAttribute("count", count);
//    Viewable view = new Viewable("",adapter.convertInversely(configs.toArray(new PersistentReportConfig[configs.size()])));
//    responseBuilder.entity(view);
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
    com.smartitengineering.generator.engine.domain.ReportConfig persistentReportConfig = adapter.convert(reportConfig);
    try {
      basicPost(persistentReportConfig);
    }
    catch (Exception ex) {
      servletRequest.setAttribute("error", ex.getMessage());
    }

    return responseBuilder.build();

  }

  public void basicPost(com.smartitengineering.generator.engine.domain.ReportConfig reportConfig) {
    Services.getInstance().getReportConfigService().save(reportConfig);
  }

  @Override
  protected String getAuthor() {
    return "Smart Generator";
  }
}
