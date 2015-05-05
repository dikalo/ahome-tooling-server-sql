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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.ait.tooling.common.api.java.util.StringOps;

@ManagedResource(objectName = "com.ait.tooling.server.sql.support.spring:name=GSQLProvider", description = "Manage SQLDescriptors.")
public class GSQLProvider implements BeanFactoryAware, IGSQLProvider
{
    private static final Logger                   logger        = Logger.getLogger(GSQLProvider.class);

    private final HashMap<String, IGSQLDescriptor> m_descriptors = new HashMap<String, IGSQLDescriptor>();

    @Override
    public IGSQLDescriptor getSQLDescriptor(String name)
    {
        name = StringOps.toTrimOrNull(name);

        if (null != name)
        {
            return m_descriptors.get(name);
        }
        return null;
    }

    @Override
    public Collection<String> keys()
    {
        return m_descriptors.keySet();
    }

    @Override
    public Collection<IGSQLDescriptor> values()
    {
        return m_descriptors.values();
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (String name : ((DefaultListableBeanFactory) factory).getBeansOfType(IGSQLDescriptor.class).keySet())
            {
                name = StringOps.toTrimOrNull(name);

                if (null != name)
                {
                    final IGSQLDescriptor descriptor = factory.getBean(name, IGSQLDescriptor.class);

                    if (null != descriptor)
                    {
                        descriptor.setName(name);

                        logger.info("Found ISQLDescriptor(" + name + ") class " + descriptor.getClass().getName());

                        m_descriptors.put(name, descriptor);
                    }
                }
            }
        }
    }

    @Override
    @ManagedOperation(description = "Close all SQLDescriptors")
    public void close() throws IOException
    {
        for (IGSQLDescriptor item : values())
        {
            try
            {
                item.close();
            }
            catch (Exception e)
            {
                logger.error("Error closing ", e);
            }
        }
    }

    @Override
    public String getDefaultSQLDescriptorName()
    {
        return GSQLContextInstance.get().getPropertyByName("sqlprovider.default.name");
    }
}
