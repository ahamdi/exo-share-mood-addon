package org.exoplatform.addons.sharemood.services;

import org.exoplatform.addons.sharemood.entity.MoodEntity;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Path("/sharemood/")
public class ShareMooodRestService implements ResourceContainer {
    Log LOG = ExoLogger.getExoLogger(ShareMooodRestService.class);

    @GET
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response updateMood(@QueryParam("mood") String mood) {
      try {
        String loggedInUser = ConversationState.getCurrent().getIdentity().getUserId();
        PortalContainer portalContainer = PortalContainer.getInstance();
        MoodService moodService = portalContainer.getComponentInstanceOfType(MoodService.class);
        MoodDTO moodDTO = moodService.saveMood(MoodEntity.Mood.valueOf(mood.toUpperCase()), loggedInUser);
        //LOG.info("Mood Updated to " + moodDTO.getMood() + " for user " + moodDTO.getUsername());
        return Response.ok("Mood Updated to " + moodDTO.getMood() + " for user " + moodDTO.getUsername()
                , MediaType.APPLICATION_JSON_TYPE).build();
      } catch (Exception e){
        LOG.error("Error while calling /rest/sharemood/update Rest service",e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
    }
    @GET
    @Path("load")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadMoods(@QueryParam("username") String username, @QueryParam("since") String since) {
      try {
        PortalContainer portalContainer = PortalContainer.getInstance();
        MoodService moodService = portalContainer.getComponentInstanceOfType(MoodService.class);
        Calendar sinceDate = Calendar.getInstance();
        sinceDate.set(Calendar.WEEK_OF_YEAR, sinceDate.getWeekYear() - 1 );
        if (since != null) {
          switch (since.toLowerCase()) {
            case "lastweek":
              sinceDate.set(Calendar.WEEK_OF_MONTH, sinceDate.get(Calendar.WEEK_OF_MONTH) - 1);
              LOG.info("Time selected {}", sinceDate.getTime());
              break;
            case "lastmonth":
              sinceDate.set(Calendar.MONTH, sinceDate.get(Calendar.MONTH) - 1);
              LOG.info("Time selected {}", sinceDate.getTime());
              break;
            case "lastquarter":
              sinceDate.set(Calendar.MONTH, sinceDate.get(Calendar.MONTH) - 3);
              LOG.info("Time selected {}", sinceDate.getTime());
              break;
            case "lastsimester":
              sinceDate.set(Calendar.MONTH, sinceDate.get(Calendar.MONTH) - 6);
              LOG.info("Time selected {}", sinceDate.getTime());
              break;
          }
        }
        JSONArray jsonArray = new JSONArray();
        for(MoodEntity.Mood mood : MoodEntity.Mood.values()) {
          List<MoodDTO> moods = moodService.loadMoods(username, mood, sinceDate);
          JSONObject object = new JSONObject();
          object.put("mood",mood);
          object.put("count",moods.size());
          object.put("period",since);
          jsonArray.put(object);
        }

        return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e){
        LOG.error("Error while calling /rest/sharemood/load Rest service",e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
    }

  @GET
  @Path("loadtoday")
  @Produces(MediaType.APPLICATION_JSON)
  public Response loadMoodOfToday(@QueryParam("username") String username) {
    PortalContainer portalContainer = PortalContainer.getInstance();
    MoodService moodService = portalContainer.getComponentInstanceOfType(MoodService.class);
    MoodDTO moodOfToday = moodService.find(username, Calendar.getInstance());
    JSONObject object = new JSONObject();
    try {
      if (moodOfToday != null){
        object.put("mood",moodOfToday.getMood());
      }
      return Response.ok(object.toString(), MediaType.APPLICATION_JSON).build();
    } catch (JSONException e) {
      LOG.error("Could not check the saved mood of user {} ", username, e);
      return Response.serverError().build();
    }

  }
}

