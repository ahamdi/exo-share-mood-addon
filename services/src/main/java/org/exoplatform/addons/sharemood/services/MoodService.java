package org.exoplatform.addons.sharemood.services;

import org.exoplatform.addons.sharemood.entity.MoodEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class MoodService {
    private final MoodDAO moodDao;
    Log LOG = ExoLogger.getExoLogger(MoodService.class);

    public MoodService(MoodDAO moodDAO) {
        this.moodDao = moodDAO;
    }

    public MoodDTO saveMood(MoodEntity.Mood mood, String userName){
        Calendar now = Calendar.getInstance();
        MoodEntity moodEntity = moodDao.findByUserAndTime(userName,now);
        if(moodEntity == null) {
            moodEntity = new MoodEntity();
            moodEntity.setWhen(Calendar.getInstance());
            moodEntity.setUserName(userName);
            moodEntity.setSelectedMood(mood);
            moodDao.create(moodEntity);
        } else {
            moodEntity.setSelectedMood(mood);
            moodDao.update(moodEntity);
        }
        return convertToDTO(moodEntity);
    }

    private MoodDTO convertToDTO(MoodEntity moodEntity) {
      if(moodEntity != null) {
        return new MoodDTO(moodEntity.getUserName(), moodEntity.getSelectedMood(), moodEntity.getWhen());
      }
      return null;
    }

    private MoodEntity convertToEntity(MoodDTO moodDTO) {
      MoodEntity moodEntity = new MoodEntity();
      moodEntity.setSelectedMood(moodDTO.getMood());
      moodEntity.setUserName(moodDTO.getUsername());
      moodEntity.setWhen(moodDTO.getWhen());
      return moodEntity;
  }
  public List<MoodDTO> loadMoods(String userName) {
    List<MoodEntity> moods = moodDao.loadMoods(userName);
    return moods.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public List<MoodDTO> loadMoods(String userName, MoodEntity.Mood mood) {
    List<MoodEntity> moods = moodDao.loadMoods(userName, mood);
    return moods.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public MoodDTO find(String userName, Calendar today){
    return convertToDTO(moodDao.findByUserAndTime(userName,today));
  }
}
