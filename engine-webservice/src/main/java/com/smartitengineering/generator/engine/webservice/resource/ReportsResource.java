/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.resource;

import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.generator.engine.service.ReportFilter;
import com.smartitengineering.generator.engine.service.factory.Services;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.commons.codec.binary.StringUtils;

/**
 *
 * @author saumitra
 */
public class ReportsResource extends AbstractResource {

  public static final String REPORT_PATH = "i/{reportContentId}";
  private ReportFilter filter;

  public ReportFilter getFilter() {
    if (this.filter == null) {
      this.filter = new ReportFilter();
    }
    return filter;
  }

  public void setReportFilter(ReportFilter filter) {
    if (filter != null) {
      this.filter = filter;
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    Response.ResponseBuilder builder = Response.ok();
    Collection<Content> reports = Services.getInstance().getReportService().search(filter);
    Feed feed = getFeed("Reports");
    //Add entries
    if (reports != null && !reports.isEmpty()) {
      for (Content content : reports) {
        Entry contentEntry = getEntry(content.getContentId().toString(), content.getContentId().toString(), content.
            getLastModifiedDate(), getLink(getRelativeURIBuilder().path(getUriInfo().getPath()).path(REPORT_PATH).build(
            StringUtils.newStringUtf8(content.getContentId().getId())), "report", MediaType.APPLICATION_ATOM_XML));
        feed.addEntry(contentEntry);
      }
    }
    return builder.build();
  }

  @GET
  @Path(REPORT_PATH)
  public ReportResource getReport() {
    return getResourceContext().getResource(ReportResource.class);
  }

  @Override
  protected String getAuthor() {
    return "Smart Generator Engine";
  }
}
