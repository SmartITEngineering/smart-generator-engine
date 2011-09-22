/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.resource;

import com.smartitengineering.generator.engine.service.ReportConfigFilter;
import com.smartitengineering.generator.engine.webservice.domain.ReportConfig;
import com.smartitengineering.generator.engine.service.factory.Services;
import com.smartitengineering.generator.engine.webservice.adapter.ReportConfigAdapterHelper;
import com.smartitengineering.util.bean.adapter.GenericAdapterImpl;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import java.util.Collection;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
public class ReportConfigsResource extends AbstractResource {

  @Context
  private HttpServletRequest servletRequest;
  private GenericAdapterImpl<ReportConfig, com.smartitengineering.generator.engine.domain.ReportConfig> adapter;

  public ReportConfigsResource() {
    adapter = new GenericAdapterImpl<ReportConfig, com.smartitengineering.generator.engine.domain.ReportConfig>();
    adapter.setHelper(new ReportConfigAdapterHelper());
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get(@DefaultValue("10") @QueryParam("count") Integer count,
                      @DefaultValue("0") @QueryParam("start") Integer index,
                      @QueryParam("startDate") Date startDate, @QueryParam("endDate") Date endDate,
                      @QueryParam("nameLike") String nameLike) {
    ResponseBuilder responseBuilder = Response.status(Status.OK);
    Feed atomFeed = getFeed("ReportConfigs", new Date());
    ReportConfigFilter reportConfigFilter = new ReportConfigFilter();
    reportConfigFilter.setNameLike(nameLike);
    reportConfigFilter.setScheduleRangeStart(startDate);
    reportConfigFilter.setScheduleRangeEnd(endDate);
    Collection<com.smartitengineering.generator.engine.domain.ReportConfig> reportConfigs = Services.getInstance().
        getReportConfigService().searchConfigs(reportConfigFilter);
    if (reportConfigs != null && !reportConfigs.isEmpty()) {
      for (com.smartitengineering.generator.engine.domain.ReportConfig reportConfig : reportConfigs) {
        Entry reportConfigEntry = getAbderaFactory().newEntry();
        reportConfigEntry.setId(reportConfig.getId());
        reportConfigEntry.setTitle(reportConfig.getName());

        Link reportConfigLink = getAbderaFactory().newLink();
        reportConfigLink.setHref(getRelativeURIBuilder().path(ReportConfigResource.class).build(reportConfig.getId()).
            toString());
        reportConfigLink.setRel(Link.REL_ALTERNATE);
        reportConfigLink.setMimeType(MediaType.APPLICATION_ATOM_XML);

        reportConfigEntry.addLink(reportConfigLink);
        atomFeed.addEntry(reportConfigEntry);
      }
    }
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
