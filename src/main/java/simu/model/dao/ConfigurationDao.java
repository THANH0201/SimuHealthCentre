package simu.model.dao;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import simu.model.datasource.MariaDbJpaConnection;
import simu.model.entity.Configuration;

import java.util.List;

public class ConfigurationDao {

    public void persist(Configuration confg) {
        EntityManager entityManager = MariaDbJpaConnection.getInstance();
        entityManager.getTransaction().begin();
        entityManager.persist(confg);
        entityManager.getTransaction().commit();
    }

    public List<Configuration> getConfigurations() {
        EntityManager entityManager = MariaDbJpaConnection.getInstance();
        TypedQuery<Configuration> query = entityManager.createQuery("SELECT c FROM Configuration c", Configuration.class);
        return query.getResultList();
    }

    public void update(Configuration confg) {
        EntityManager entityManager = MariaDbJpaConnection.getInstance();
        entityManager.getTransaction().begin();
        entityManager.merge(confg);
        entityManager.getTransaction().commit();
    }

    public void delete(Configuration confg) {
        EntityManager entityManager = MariaDbJpaConnection.getInstance();
        entityManager.getTransaction().begin();
        entityManager.remove(confg);
        entityManager.getTransaction().commit();
    }
    //test exist currency
    public boolean existsByType(String type) {
        EntityManager entityManager = MariaDbJpaConnection.getInstance();
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Configuration c WHERE c.type = :type", Long.class);
        query.setParameter("type", type);
        Long count = query.getSingleResult();
        return count > 0;
    }

}
