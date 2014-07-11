package com.anagorny.chaters.DAO;

import com.anagorny.chaters.mapObj.User;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sosnov on 05.04.14.
 */
public class UserDAO implements DAO {
    private Session session = null;

    @Override
    public void addObj(Object obj) throws SQLException {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при User::addObj(Object obj)");
        } finally {
            if (session != null && session.isOpen()) {

                session.close();
            }
        }
    }

    @Override
    public void updateObj(Object obj) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            //   obj=session.merge(obj);
            session.update(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage() + "\nОшибка при User::updateObj(Object obj)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public User getObjById(Long obj_id) throws SQLException {
        Session session = null;
        User usr = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            usr = (User) session.load(User.class, obj_id);
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при User::getObjById");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return usr;
    }

    @Override
    public Collection getAllObj() throws SQLException {
        Session session = null;
        List users = new ArrayList<User>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            users = session.createCriteria(User.class).list();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при User::getAllObj()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return users;
    }

    @Override
    public void deleteObj(Object usr) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(usr);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при User::deleteObj(Object bus)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    /*
    комнаты пользователя
     */

    public Collection getRoomsByUsrs(User usr) throws SQLException {
        Session session = null;
        List rooms = new ArrayList<User>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long usr_id = usr.getUser_id();
            Query query = session.createQuery("select r  from Room r INNER JOIN r.rooms rms where rms.user_id = :user_Id").setLong("user_Id", usr_id);
            rooms = (List<User>) query.list();
            session.getTransaction().commit();

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return rooms;
    }

    public User getUsrFromNick(String nick) throws SQLException {
        Session session = null;
        ArrayList usrs = new ArrayList<User>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query query = session.createQuery("select u  from User u where u.user_nick = :user_nick").setString("user_nick", nick);
            usrs = (ArrayList<User>) query.list();
            session.getTransaction().commit();
             if (usrs.size()!=1){
                 throw new SQLException("Get many data from Data Base with user_nick "+nick);
             }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return (User)usrs.get(0);
    }

 /*
    public void AddRoom(User usr,Room room) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            HashSet<Room> rms= (HashSet<Room>) usr.getRooms();
            rms.add(room);
            usr.setRooms(rms);
            session.update(usr);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при User::AddRoom(User usr,Room room)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
 */

}
