package org.exoplatform.addons.sharemood.services;

import org.exoplatform.addons.sharemood.entity.MoodEntity;
import org.exoplatform.commons.utils.CommonsUtils;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Path("/sharemood/")
public class ShareMoodRestService implements ResourceContainer {
  private static final String TRENDS = "trends";
  Log LOG = ExoLogger.getExoLogger(ShareMoodRestService.class);

    @GET
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    public Response updateMood(@QueryParam("mood") String mood) {
      try {
        String loggedInUser = ConversationState.getCurrent().getIdentity().getUserId();
        MoodService moodService = CommonsUtils.getService(MoodService.class);
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
    public Response loadMoods(@QueryParam("username") String username, @QueryParam("since") String since, @QueryParam("chartType") String chartType) {
      try {
        MoodService moodService = CommonsUtils.getService(MoodService.class);
        Calendar sinceDate = Calendar.getInstance();
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
        long totalMoods = moodService.countAllMoodsByUser(username, sinceDate);
        JSONArray jsonArray = new JSONArray();
        if(TRENDS.equalsIgnoreCase(chartType)) {
          List<MoodDTO> moods = moodService.findAllByUserAndSince(username, sinceDate);
          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
          for(MoodDTO mood : moods){
            JSONObject object = new JSONObject();
            object.put("mood", mood.getMood().name());
            object.put("when", format.format(mood.getWhen().getTime()));
            object.put("value", mood.getMood().ordinal());
            jsonArray.put(object);
          }
        } else {
          for (MoodEntity.Mood mood : MoodEntity.Mood.values()) {
            List<MoodDTO> moods = moodService.loadMoods(username, mood, sinceDate);
            JSONObject object = new JSONObject();
            object.put("mood", mood.name());
            object.put("count", moods.size());
            object.put("percent", (moods.size() * 100) / totalMoods);
            jsonArray.put(object);
          }
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
    MoodService moodService = CommonsUtils.getService(MoodService.class);
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

