/*
 * Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.tooling.server.sql.support.spring;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.ait.tooling.common.api.java.util.StringOps;
import com.ait.tooling.common.api.types.Activatable;
import com.ait.tooling.server.sql.GSQL;
import com.ait.tooling.server.sql.IGSQLPreProcessConnectionHandler;
import com.ait.tooling.server.sql.IGSQLRowObjectMapper;
import com.ait.tooling.server.sql.IGSQLStatementSetObjectHandler;

@ManagedResource
public class GSQLDescriptor extends Activatable implements IGSQLDescriptor
{
    private static final long                      serialVersionUID = -8176488950309932959L;

    private String                                 m_name;

    private final DataSource                       m_data_source;

    private List<IGSQLStatementSetObjectHandler>   m_setobj_list;

    private List<IGSQLPreProcessConnectionHandler> m_precon_list;

    private IGSQLRowObjectMapper                   m_row_object_mapper;

    private String                                 m_description    = "Generic GSQLDescriptor";

    public GSQLDescriptor(final DataSource datasource)
    {
        super(true);

        m_data_source = Objects.requireNonNull(datasource);
    }

    @Override
    public DataSource getDataSource()
    {
        return m_data_source;
    }

    @Override
    public void setStatementObjectHandlers(final List<IGSQLStatementSetObjectHandler> list)
    {
        m_setobj_list = list;
    }

    @Override
    public void setPreProcessConnectionHandlers(final List<IGSQLPreProcessConnectionHandler> list)
    {
        m_precon_list = list;
    }

    @Override
    @ManagedOperation(description = "Get GSQLDescriptor name.")
    public final String getName()
    {
        return m_name;
    }

    @Override
    public final void setName(final String name)
    {
        m_name = StringOps.requireTrimOrNull(name, "GSQLDescriptor name is null");
    }

    @Override
    public GSQL make()
    {
        if (false == isActive())
        {
            throw new IllegalArgumentException("GSQLDescriptor [" + getDescription() + "] is not active.");
        }
        final GSQL gsql = new GSQL(getDataSource());

        gsql.setStatementSetObjectHandlers(getStatementSetObjectHandlers());

        gsql.setPreProcessConnectionHandlers(getPreProcessConnectionHandlers());

        return gsql;
    }

    @Override
    public void close() throws IOException
    {
        setActive(false);
    }

    @Override
    public void setDescription(String description)
    {
        description = StringOps.toTrimOrNull(description);

        if (null != description)
        {
            m_description = description;
        }
    }

    @Override
    @ManagedOperation(description = "Get GSQLDescriptor description.")
    public String getDescription()
    {
        return m_description;
    }

    @Override
    public IGSQLRowObjectMapper getRowObjectMapper()
    {
        return m_row_object_mapper;
    }

    @Override
    public void setRowObjectMapper(final IGSQLRowObjectMapper row_object_mapper)
    {
        m_row_object_mapper = row_object_mapper;
    }

    @Override
    public List<IGSQLStatementSetObjectHandler> getStatementSetObjectHandlers()
    {
        return m_setobj_list;
    }

    @Override
    public List<IGSQLPreProcessConnectionHandler> getPreProcessConnectionHandlers()
    {
        return m_precon_list;
    }

    @Override
    @ManagedOperation(description = "Is GSQLDescriptor active.")
    public final boolean isActive()
    {
        return super.isActive();
    }

    @Override
    @ManagedOperation(description = "Set GSQLDescriptor active.")
    public final boolean setActive(final boolean active)
    {
        return super.setActive(active);
    }
}
