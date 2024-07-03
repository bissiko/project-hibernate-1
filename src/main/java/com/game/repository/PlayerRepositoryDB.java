package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;
    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        //properties.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        //properties.put(Environment.URL, "jdbc:mysql://localhost:3307/rpg") ;
        properties.put(Environment.USER, "bissiko");
        properties.put(Environment.PASS, "Mys__3299");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3307/rpg");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT * FROM rpg.player";
            NativeQuery<Player> query = session.createNativeQuery(hql, Player.class);
            query.setFirstResult(pageSize*pageNumber + 1);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            //String hql = "select count(*) from Player";
            Query<Integer> query = session.createNamedQuery("Player_Count", Integer.class);
            return query.uniqueResult();
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Long id = (Long) session.save(player);
            transaction.commit();
        }
        return player;
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
        }
        return player;
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            //String hql = "from Player WHERE id = :id";
            //Query<Player> query = session.createQuery(hql, Player.class);
            //query.setParameter("id", id );
            //Optional<Player> list = query.list();

            return Optional.ofNullable(session.get(Player.class, id));
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}