/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.tooling.server.sql;

import groovy.sql.GroovyResultSet;
import groovy.sql.GroovyRowResult;
import groovy.sql.InOutParameter;
import groovy.sql.InParameter;
import groovy.sql.OutParameter;
import groovy.sql.ResultSetOutParameter;
import groovy.sql.Sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ait.tooling.json.JSONArray;
import com.ait.tooling.json.JSONObject;

public class GSQL extends Sql
{
    private static final Logger                    logger = Logger.getLogger(GSQL.class);

    private static IGSQLRowObjectMapper            s_default_row_object_mapper;

    private List<IGSQLStatementSetObjectHandler>   m_setobj_list;

    private List<IGSQLPreProcessConnectionHandler> m_precon_list;

    public static final InParameter GSQLINPARAMETER(final int type, final Object value)
    {
        return in(type, value);
    }

    public static final OutParameter GSQLOUTPARAMETER(final int type)
    {
        return out(type);
    }

    public static final InOutParameter GSQLINOUTPARAMETER(final int type, final Object value)
    {
        return inout(in(type, value));
    }

    public static final InOutParameter GSQLINOUTPARAMETER(final InParameter in)
    {
        return inout(in);
    }

    public static final ResultSetOutParameter GSQLRESULTSETOUTPARAMETER(final int type)
    {
        return new ResultSetOutParameter()
        {
            @Override
            public final int getType()
            {
                return type;
            }
        };
    }

    public static final void setDefaultRowObjectMapper(final IGSQLRowObjectMapper mapper)
    {
        s_default_row_object_mapper = mapper;
    }

    public GSQL(final DataSource ds)
    {
        super(Objects.requireNonNull(ds, "DataSource was null"));
    }

    public void setStatementSetObjectHandlers(final List<IGSQLStatementSetObjectHandler> list)
    {
        m_setobj_list = list;
    }

    public void setPreProcessConnectionHandlers(final List<IGSQLPreProcessConnectionHandler> list)
    {
        m_precon_list = list;
    }

    /*
     * Override protected setObject() to interpose a list of ISQLSetObjectHandler, which can examine the value class
     * and do something special, i.e. Streams, Cursors, RAW, etc. If a handler returns true then it has done it's own
     * setObject() on the PreparedStatement.
     * (non-Javadoc)
     * @see groovy.sql.Sql#setObject(java.sql.PreparedStatement, int, java.lang.Object)
     */

    @Override
    protected void setObject(final PreparedStatement statement, final int i, final Object value) throws SQLException
    {
        if ((null == m_setobj_list) || (m_setobj_list.isEmpty()))
        {
            super.setObject(statement, i, value);
        }
        else
        {
            boolean done = false;

            for (IGSQLStatementSetObjectHandler handler : m_setobj_list)
            {
                if (handler.setObject(statement, i, value))
                {
                    done = true;

                    break;
                }
            }
            if (false == done)
            {
                super.setObject(statement, i, value);
            }
        }
    }

    private final long _now_() // a bit hacky - I don't have a Duration class that I like anymore... DSJ
    {
        return System.currentTimeMillis();
    }

    @Override
    protected Connection createConnection() throws SQLException
    {
        final long time = _now_();

        if ((null == m_precon_list) || (m_precon_list.isEmpty()))
        {
            final Connection connection = super.createConnection();

            logger.info("Time to get a default connection " + (_now_() - time) + "ms");

            return connection;
        }
        Connection connection = super.createConnection();

        for (IGSQLPreProcessConnectionHandler handler : m_precon_list)
        {
            connection = handler.preProcessConnection(connection);
        }
        logger.info("Time to get a processed connection " + (_now_() - time) + "ms");

        return connection;
    }

    public static final JSONObject TOJSONOBJECT(final GroovyRowResult result) throws SQLException
    {
        return TOJSONOBJECT(result, s_default_row_object_mapper);
    }

    public static final JSONObject TOJSONOBJECT(final GroovyRowResult result, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(result, "GroovyRowResult was null");

        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        final JSONObject object = new JSONObject();

        if (null == mapper)
        {
            for (Object ikey : result.keySet())
            {
                object.put(ikey.toString(), result.get(ikey));
            }
        }
        else
        {
            for (Object ikey : result.keySet())
            {
                mapper.map(object, ikey.toString(), result.get(ikey));
            }
        }
        return object;
    }

    public static final JSONObject TOJSONOBJECT(final GroovyResultSet rset) throws SQLException
    {
        return TOJSONOBJECT(rset, s_default_row_object_mapper);
    }

    public static final JSONObject TOJSONOBJECT(final GroovyResultSet rset, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(rset, "GroovyResultSet was null");

        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        final JSONObject object = new JSONObject();

        final ResultSetMetaData meta = rset.getMetaData();

        final int cols = meta.getColumnCount();

        if (cols < 1)
        {
            return object;
        }
        if (null == mapper)
        {
            for (int i = 1; i <= cols; i++)
            {
                object.put(meta.getColumnLabel(i), rset.getObject(i));
            }
        }
        else
        {
            for (int i = 1; i <= cols; i++)
            {
                mapper.map(object, meta.getColumnLabel(i), rset.getObject(i));
            }
        }
        return object;
    }

    public static final JSONArray TOJSONARRAY(final List<GroovyRowResult> list) throws SQLException
    {
        return TOJSONARRAY(list, s_default_row_object_mapper);
    }

    public static final JSONArray TOJSONARRAY(final List<GroovyRowResult> list, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(list, "List<GroovyRowResult> was null");

        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        final JSONArray array = new JSONArray();

        for (GroovyRowResult result : list)
        {
            array.add(TOJSONOBJECT(result, mapper));
        }
        return array;
    }

    public static final JSONArray TOJSONARRAY(GroovyResultSet rset) throws SQLException
    {
        return TOJSONARRAY(rset, s_default_row_object_mapper);
    }

    public static final JSONArray TOJSONARRAY(final GroovyResultSet rset, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(rset, "GroovyResultSet was null");

        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        final JSONArray array = new JSONArray();

        final ResultSetMetaData meta = rset.getMetaData();

        final int cols = meta.getColumnCount();

        if (cols < 1)
        {
            return array;
        }
        final String[] labs = new String[cols];

        for (int i = 1; i <= cols; i++)
        {
            labs[i - 1] = meta.getColumnLabel(i);
        }
        while (rset.next())
        {
            final JSONObject object = new JSONObject();

            if (null == mapper)
            {
                for (int i = 1; i <= cols; i++)
                {
                    object.put(labs[i - 1], rset.getObject(i));
                }
            }
            else
            {
                for (int i = 1; i <= cols; i++)
                {
                    mapper.map(object, labs[i - 1], rset.getObject(i));
                }
            }
            array.add(object);
        }
        return array;
    }
}
