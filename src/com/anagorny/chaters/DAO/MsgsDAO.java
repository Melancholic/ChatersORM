package com.anagorny.chaters.DAO;

import com.anagorny.chaters.mapObj.Messege;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sosnov on 10.04.14.
 */
public class MsgsDAO implements DAO {
    private Session session = null;

    @Override
    public void addObj(Object obj) throws SQLException {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при MsgsDAO::addObj(Object obj)");
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
            //session.merge(obj);
            session.update(obj);
            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println(e.getMessage() + "\nОшибка при Msgs::updateObj(Object obj)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public Messege getObjById(Long obj_id) throws SQLException {
        Session session = null;
        Messege msg = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            msg = (Messege) session.load(Messege.class, obj_id);
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при Msgs::getObjById");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return msg;
    }

    @Override
    public Collection getAllObj() throws SQLException {
        Session session = null;
        List msgs = new ArrayList<Messege>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            msgs = session.createCriteria(Messege.class).list();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при  Msgs::getAllObj()");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return msgs;
    }

    @Override
    public void deleteObj(Object obj) throws SQLException {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(obj);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e.getMessage() + "\nОшибка при Msg::deleteObj(Object obj)");
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

}
