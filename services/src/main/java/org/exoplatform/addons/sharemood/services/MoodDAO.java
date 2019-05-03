package org.exoplatform.addons.sharemood.services;

import org.exoplatform.addons.sharemood.entity.MoodEntity;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MoodDAO extends GenericDAOJPAImpl<MoodEntity, Long> {

    public MoodEntity findByUserAndTime(String userName, Calendar when) {
        try {
            return getEntityManager().createNamedQuery("MoodEntity.findByUsernameAndDate", MoodEntity.class).setParameter("userName", userName)
                    .setParameter("when", when, TemporalType.DATE).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public List<MoodEntity> loadMoods(String userName) {
        return getEntityManager().createNamedQuery("MoodEntity.findByUsername",MoodEntity.class)
                .setParameter("username",userName).getResultList();
    }
    public List<MoodEntity> loadMoods(String userName, MoodEntity.Mood mood) {
        return getEntityManager().createNamedQuery("MoodEntity.findByUsernameAndMood",MoodEntity.class)
                .setParameter("userName",userName)
                .setParameter("mood",mood).getResultList();
    }
    public List<MoodEntity> loadMoods(String userName, MoodEntity.Mood mood, Calendar since) {
        return getEntityManager().createNamedQuery("MoodEntity.findByUsernameAndMoodAndSince",MoodEntity.class)
                .setParameter("userName",userName)
                .setParameter("since",since)
                .setParameter("mood",mood).getResultList();
    }
    public Long countMoodsByUser (String userName, Calendar since) {
      return getEntityManager().createNamedQuery("MoodEntity.countAllMoodsByUser", Long.class)
          .setParameter("userName", userName)
          .setParameter("since", since)
          .getSingleResult();
    }
    public List<MoodEntity> findAllMoodsByUserAndSince (String userName, Calendar since) {
      return getEntityManager().createNamedQuery("MoodEntity.findAllByUserAndSince", MoodEntity.class)
          .setParameter("userName", userName)
          .setParameter("since", since)
          .getResultList();
    }
}
