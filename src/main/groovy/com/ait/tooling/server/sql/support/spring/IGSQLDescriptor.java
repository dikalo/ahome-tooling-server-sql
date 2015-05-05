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

package com.ait.tooling.server.sql.support.spring;

import java.io.Closeable;
import java.util.List;

import javax.sql.DataSource;

import com.ait.tooling.common.api.types.IActivatable;
import com.ait.tooling.common.api.types.INamedDefinition;
import com.ait.tooling.server.sql.GSQL;
import com.ait.tooling.server.sql.IGSQLPreProcessConnectionHandler;
import com.ait.tooling.server.sql.IGSQLRowObjectMapper;
import com.ait.tooling.server.sql.IGSQLStatementSetObjectHandler;

public interface IGSQLDescriptor extends INamedDefinition, IActivatable, Closeable
{
    public DataSource getDataSource();

    public void setDescription(String description);

    public String getDescription();

    public void setStatementObjectHandlers(List<IGSQLStatementSetObjectHandler> list);

    public List<IGSQLStatementSetObjectHandler> getStatementSetObjectHandlers();

    public void setPreProcessConnectionHandlers(List<IGSQLPreProcessConnectionHandler> list);

    public List<IGSQLPreProcessConnectionHandler> getPreProcessConnectionHandlers();

    public IGSQLRowObjectMapper getRowObjectMapper();

    public void setRowObjectMapper(IGSQLRowObjectMapper row_object_mapper);

    public GSQL make();
}