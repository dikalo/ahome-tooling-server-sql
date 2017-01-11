/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.ait.tooling.server.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import com.ait.tooling.common.api.java.util.StringOps;
import com.ait.tooling.server.core.json.JSONArray;
import com.ait.tooling.server.core.json.JSONObject;

import groovy.sql.GroovyResultSet;
import groovy.sql.GroovyRowResult;
import groovy.sql.InOutParameter;
import groovy.sql.InParameter;
import groovy.sql.OutParameter;
import groovy.sql.ResultSetOutParameter;
import groovy.sql.Sql;

public class GSQL extends Sql
{
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

    @Override
    protected void closeResources(final Connection connection, final Statement statement, final ResultSet results)
    {
        super.closeResources(connection, statement, results);
    }

    @Override
    protected void closeResources(final Connection connection, final Statement statement)
    {
        super.closeResources(connection, statement);
    }

    @Override
    protected void closeResources(final Connection connection)
    {
        super.closeResources(connection);
    }

    @Override
    protected Connection createConnection() throws SQLException
    {
        if ((null == m_precon_list) || (m_precon_list.isEmpty()))
        {
            final Connection connection = super.createConnection();

            return connection;
        }
        Connection connection = super.createConnection();

        for (IGSQLPreProcessConnectionHandler handler : m_precon_list)
        {
            handler.preProcessConnection(connection);
        }
        return connection;
    }

    @Override
    protected AbstractQueryCommand createQueryCommand(final String sql)
    {
        return super.createQueryCommand(sql);
    }

    @Override
    protected AbstractQueryCommand createPreparedQueryCommand(String sql, List<Object> queryParams)
    {
        return super.createPreparedQueryCommand(sql, queryParams);
    }

    public static final JSONObject json(final GroovyRowResult result) throws SQLException
    {
        return json(result, s_default_row_object_mapper);
    }

    public static final JSONObject json(final GroovyRowResult result, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(result, "GroovyRowResult was null");

        final JSONObject object = new JSONObject();

        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        if (null == mapper)
        {
            for (Object ikey : result.keySet())
            {
                final String name = StringOps.toTrimOrNull(ikey.toString());

                if (null != name)
                {
                    object.put(name, result.get(ikey));
                }
            }
        }
        else
        {
            for (Object ikey : result.keySet())
            {
                final String name = StringOps.toTrimOrNull(ikey.toString());

                if (null != name)
                {
                    mapper.mapObject(object, name, result.get(ikey));
                }
            }
        }
        return object;
    }

    public static final JSONObject json(final GroovyResultSet rset) throws SQLException
    {
        return json(rset, s_default_row_object_mapper);
    }

    public static final JSONObject json(final GroovyResultSet rset, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(rset, "GroovyResultSet was null");

        final JSONObject object = new JSONObject();

        final ResultSetMetaData meta = rset.getMetaData();

        final int cols = meta.getColumnCount();

        if (cols < 1)
        {
            return object;
        }
        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        if (null == mapper)
        {
            for (int i = 1; i <= cols; i++)
            {
                final String name = StringOps.toTrimOrNull(meta.getColumnLabel(i));

                if (null != name)
                {
                    object.put(name, rset.getObject(i));
                }
            }
        }
        else
        {
            for (int i = 1; i <= cols; i++)
            {
                final String name = StringOps.toTrimOrNull(meta.getColumnLabel(i));

                if (null != name)
                {
                    mapper.mapObject(object, name, rset.getObject(i));
                }
            }
        }
        return object;
    }

    public static final JSONArray jarr(final List<GroovyRowResult> list) throws SQLException
    {
        return jarr(list, s_default_row_object_mapper);
    }

    public static final JSONArray jarr(final List<GroovyRowResult> list, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(list, "List<GroovyRowResult> was null");

        final JSONArray array = new JSONArray();

        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        for (GroovyRowResult result : list)
        {
            array.add(json(result, mapper));
        }
        return array;
    }

    public static final JSONArray jarr(GroovyResultSet rset) throws SQLException
    {
        return jarr(rset, s_default_row_object_mapper);
    }

    public static final JSONArray jarr(final GroovyResultSet rset, IGSQLRowObjectMapper mapper) throws SQLException
    {
        Objects.requireNonNull(rset, "GroovyResultSet was null");

        final JSONArray array = new JSONArray();

        final ResultSetMetaData meta = rset.getMetaData();

        final int cols = meta.getColumnCount();

        if (cols < 1)
        {
            return array;
        }
        final String[] labs = new String[cols + 1];// this is hacky + 1 so I don't have to keep doing an index subtract - 1 in the loops.

        for (int i = 1; i <= cols; i++)
        {
            labs[i] = StringOps.toTrimOrNull(meta.getColumnLabel(i));
        }
        if (null == mapper)
        {
            mapper = s_default_row_object_mapper;
        }
        if (null == mapper)
        {
            while (rset.next())
            {
                final JSONObject object = new JSONObject();

                for (int i = 1; i <= cols; i++)
                {
                    final String name = labs[i];

                    if (null != name)
                    {
                        object.put(name, rset.getObject(i));
                    }
                }
                array.add(object);
            }
        }
        else
        {
            while (rset.next())
            {
                final JSONObject object = new JSONObject();

                for (int i = 1; i <= cols; i++)
                {
                    final String name = labs[i];

                    if (null != name)
                    {
                        mapper.mapObject(object, name, rset.getObject(i));
                    }
                }
                array.add(object);
            }
        }
        return array;
    }
}
