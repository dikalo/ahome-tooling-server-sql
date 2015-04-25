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

package com.ait.tooling.server.sql.support

import groovy.sql.GroovyRowResult
import groovy.transform.CompileStatic
import groovy.transform.Memoized

import com.ait.tooling.json.JSONObject
import com.ait.tooling.server.sql.GSQL
import com.ait.tooling.server.sql.support.spring.GSQLContextInstance
import com.ait.tooling.server.sql.support.spring.IGSQLContext
import com.ait.tooling.server.sql.support.spring.IGSQLDescriptor
import com.ait.tooling.server.sql.support.spring.IGSQLProvider

@CompileStatic
public trait GSQLTrait
{
    @Memoized
    public IGSQLContext getGSQLContext()
    {
        GSQLContextInstance.get()
    }
    
    @Memoized
    public IGSQLProvider getGSQLProvider()
    {
        getGSQLContext().getGSQLProvider()
    }
    
    @Memoized
    public IGSQLDescriptor getSQLDescriptor(String name)
    {
        getGSQLProvider().getSQLDescriptor(name)
    }
    
    @Memoized
    public IGSQLDescriptor getSQLDescriptor()
    {
        getGSQLProvider().getSQLDescriptor(getDefaultSQLDescriptorName())
    }
    
    @Memoized
    public String getDefaultSQLDescriptorName()
    {
        getGSQLProvider().getDefaultSQLDescriptorName()
    }
    
    public GSQL gsql(final String name)
    {
        final IGSQLDescriptor desc = getSQLDescriptor(name)
        
        if (desc)
        {
            return desc.make()
        }
        return null
    }

    public GSQL gsql()
    {
        final IGSQLDescriptor desc = getSQLDescriptor()
        
        if (desc)
        {
            return desc.make()
        }
        return null
    }
    
    public JSONObject jsql(GString query)
    {
        jrowresults(gsql().rows(query))
    }

    public JSONObject jsql(String name, GString query)
    {
        jrowresults(gsql(name).rows(query))
    }

    public JSONObject jsql(String query)
    {
        jrowresults(gsql().rows(query))
    }

    public JSONObject jsql(String name, String query)
    {
        jrowresults(gsql(name).rows(query))
    }

    public JSONObject jsql(GString query, List<?> params)
    {
        jrowresults(gsql().rows(query, params))
    }

    public JSONObject jsql(String name, GString query, List<?> params)
    {
        jrowresults(gsql(name).rows(query, params))
    }

    public JSONObject jsql(String query, List<?> params)
    {
        jrowresults(gsql().rows(query, params))
    }

    public JSONObject jsql(String name, String query, List<?> params)
    {
        jrowresults(gsql(name).rows(query, params))
    }

    public JSONObject jrowresults(List<GroovyRowResult> list)
    {
        new JSONObject(GSQL.TOJSONARRAY(list))
    }
}
