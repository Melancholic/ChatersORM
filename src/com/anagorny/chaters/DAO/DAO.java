package com.anagorny.chaters.DAO;


import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by sosnov on 05.04.14.
 */
public interface DAO {
    public void addObj(Object obj) throws SQLException;

    public void updateObj(Object obj) throws SQLException;

    public Object getObjById(Long obj_id) throws SQLException;

    public Collection getAllObj() throws SQLException;

    public void deleteObj(Object bus) throws SQLException;
}
