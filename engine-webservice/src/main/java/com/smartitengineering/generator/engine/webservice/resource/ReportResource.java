/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.generator.engine.webservice.resource;

import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.Representation;
import com.smartitengineering.cms.api.type.RepresentationDef;
import com.smartitengineering.generator.engine.service.factory.Services;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author saumitra
 */
public class ReportResource extends AbstractResource {

  @PathParam("reportContentId")
  private String reportContentId;

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response getDefault() {
    final Response.ResponseBuilder builder;
    Content content = getContent();
    if (content != null) {
      builder = Response.ok();
      Feed feed = getFeed(reportContentId, content.getLastModifiedDate());
      Map<com.smartitengineering.cms.api.common.MediaType, String> representations = content.getContentDefinition().
          getRepresentations();
      for (com.smartitengineering.cms.api.common.MediaType type : representations.keySet()) {
        final String name = representations.get(type);
        final RepresentationDef def = content.getContentDefinition().getRepresentationDefs().get(name);
        feed.addLink(getLink(getUriInfo().getRequestUri(), Link.REL_ALTERNATE, def.getMIMEType()));
      }
      builder.entity(feed);
      builder.lastModified(content.getLastModifiedDate());
      builder.tag(content.getEntityTagValue());
    }
    else {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    return builder.build();
  }

  @GET
  public Response doSmartGet() {
    final Response.ResponseBuilder builder;
    Content content = getContent();
    final Map<com.smartitengineering.cms.api.common.MediaType, String> representations = content.getContentDefinition().
        getRepresentations();
    if (representations != null && !representations.isEmpty()) {

      Set<com.smartitengineering.cms.api.common.MediaType> mediaTypes = representations.keySet();
      MediaType[] types = new MediaType[mediaTypes.size()];
      int i = 0;
      for (com.smartitengineering.cms.api.common.MediaType mediaType : mediaTypes) {
        types[i++] = MediaType.valueOf(mediaType.toString());
      }
      List<Variant> variants = Variant.mediaTypes(types).add().build();
      Variant mostAppropriateaVariant = getContext().getRequest().selectVariant(variants);
      if (mostAppropriateaVariant != null && mostAppropriateaVariant.getMediaType() != null &&
          mediaTypes.contains(com.smartitengineering.cms.api.common.MediaType.fromString(mostAppropriateaVariant.
          getMediaType().toString()))) {
        builder = Response.ok();
        String name =
               representations.get(com.smartitengineering.cms.api.common.MediaType.fromString(mostAppropriateaVariant.
            getMediaType().toString()));
        builder.type(mostAppropriateaVariant.getMediaType());
        Representation representation = content.getRepresentation(name);
        builder.entity(representation.getRepresentation());
        builder.lastModified(representation.getLastModifiedDate());
      }
      else {
        builder = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
      }
    }
    else {
      builder = Response.noContent();
    }
    return builder.build();
  }

  private Content getContent() {
    return Services.getInstance().getReportService().getReportContent(reportContentId);
  }

  @Override
  protected String getAuthor() {
    return "Smart Generator Engine";
  }
}
