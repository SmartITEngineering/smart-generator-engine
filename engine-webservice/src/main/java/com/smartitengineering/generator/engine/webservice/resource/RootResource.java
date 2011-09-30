/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.resource;

import com.smartitengineering.util.rest.atom.server.AbstractResource;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.abdera.model.Feed;

/**
 *
 * @author imyousuf
 */
@Path("/")
public class RootResource extends AbstractResource {
  
  public static final String CONFIGS = "reportconfigs";
  public static final String REPORTS = "reports";
  
  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    Response.ResponseBuilder builder = Response.ok();
    Feed feed = getFeed("root", "Smart Generator Engine - Root Resource", new Date());
    builder.entity(feed);
    feed.addLink(getLink(getRelativeURIBuilder().path(CONFIGS).build(), CONFIGS, MediaType.APPLICATION_ATOM_XML));
    CacheControl control = new CacheControl();
    control.setMaxAge(10800);
    builder.cacheControl(control);
    return builder.build();
  }
  
  @Path(CONFIGS)
  public ReportConfigsResource getConfigs() {
    return getResourceContext().getResource(ReportConfigsResource.class);
  }
  
  @Path(REPORTS)
  public ReportsResource getReports() {
    return getResourceContext().getResource(ReportsResource.class);
  }
  
  @Override
  protected String getAuthor() {
    return "Smart Generator Engine";
  }
}
