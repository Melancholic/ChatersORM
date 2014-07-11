package com.anagorny.chaters.DAO;

import com.anagorny.chaters.mapObj.Room;
import com.anagorny.chaters.mapObj.User;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sosnov on 05.04.14.
 */
public class RoomDAO implements DAO {
    private Session session = null;

    @Override
    public void addObj(Object obj) throws SQLException {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при RoomDAO::addObj(Object obj)");
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
            //  session.merge(obj);
            session.update(obj);
            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println(e.getMessage() + "\nОшибка при Rooms::updateObj(Object obj)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Room getObjById(Long obj_id) throws SQLException {
        Session session = null;
        Room rm = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            rm = (Room) session.load(Room.class, obj_id);
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при Rooms::getObjById");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return rm;
    }

    @Override
    public Collection getAllObj() throws SQLException {
        Session session = null;
        List rms = new ArrayList<Room>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            rms = session.createCriteria(Room.class).list();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при Rooms::getAllObj()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return rms;
    }

    @Override
    public void deleteObj(Object room) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(room);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при Rooms::deleteObj(Object room)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /*
    пользователи в комнате

     */
    public Collection getUsersByRoom(Room room) throws SQLException {
        Session session = null;
        List usrs = new ArrayList<User>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            System.err.println("OKOKOK");
            session.beginTransaction();
            Hibernate.initialize(room.getRoom_id());
            Long room_id = room.getRoom_id();
            Query query = session.createQuery("select u  from User u INNER JOIN u.rooms rms where rms.room_id = :roomId").setLong("roomId", room_id);
            usrs = (List<User>) query.list();
            session.getTransaction().commit();

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return usrs;
    }

    public Collection getRoomsFromParam(String param, String val) throws SQLException {
        Session session = null;
        List rooms = new ArrayList<Room>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query query = session.createQuery("select r  from Room r  where r." + param + "  = :" + param).setString(param, val);
            rooms = (List<User>) query.list();
            session.getTransaction().commit();

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return rooms;
    }
}
