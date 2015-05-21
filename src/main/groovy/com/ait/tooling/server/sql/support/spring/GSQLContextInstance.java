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

import groovy.lang.Closure;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

import com.ait.tooling.json.JSONObject;
import com.ait.tooling.server.core.jmx.management.ICoreServerManager;
import com.ait.tooling.server.core.pubsub.IPubSubDescriptorProvider;
import com.ait.tooling.server.core.pubsub.IPubSubHandlerRegistration;
import com.ait.tooling.server.core.pubsub.IPubSubMessageReceivedHandler;
import com.ait.tooling.server.core.pubsub.IPubSubStateChangedHandler;
import com.ait.tooling.server.core.pubsub.PubSubChannelType;
import com.ait.tooling.server.core.pubsub.PubSubStateType;
import com.ait.tooling.server.core.security.AuthorizationResult;
import com.ait.tooling.server.core.security.IAuthorizationProvider;
import com.ait.tooling.server.core.support.spring.IBuildDescriptorProvider;
import com.ait.tooling.server.core.support.spring.IExecutorServiceDescriptorProvider;
import com.ait.tooling.server.core.support.spring.IPropertiesResolver;
import com.ait.tooling.server.core.support.spring.IServerContext;
import com.ait.tooling.server.core.support.spring.ServerContextInstance;

public final class GSQLContextInstance implements IGSQLContext
{
    private static final long                serialVersionUID = 8487068206661824540L;

    private static final GSQLContextInstance INSTANCE         = new GSQLContextInstance();

    public static final GSQLContextInstance get()
    {
        return INSTANCE;
    }

    private GSQLContextInstance()
    {
    }

    @Override
    public IGSQLProvider getGSQLProvider()
    {
        return getBean("GSQLProvider", IGSQLProvider.class);
    }

    @Override
    public IServerContext getServerContext()
    {
        return ServerContextInstance.get();
    }

    @Override
    public WebApplicationContext getApplicationContext()
    {
        return getServerContext().getApplicationContext();
    }

    @Override
    public Environment getEnvironment()
    {
        return getServerContext().getEnvironment();
    }

    public <T> T getBean(final String name, final Class<T> type)
    {
        return getApplicationContext().getBean(name, type);
    }

    @Override
    public String getPropertyByName(final String name)
    {
        return getServerContext().getPropertyByName(name);
    }

    @Override
    public String getPropertyByName(final String name, final String otherwise)
    {
        return getServerContext().getPropertyByName(name, otherwise);
    }

    @Override
    public IAuthorizationProvider getAuthorizationProvider()
    {
        return getServerContext().getAuthorizationProvider();
    }

    @Override
    public Iterable<String> getPrincipalsKeys()
    {
        return getServerContext().getPrincipalsKeys();
    }

    @Override
    public ICoreServerManager getCoreServerManager()
    {
        return getServerContext().getCoreServerManager();
    }

    @Override
    public IExecutorServiceDescriptorProvider getExecutorServiceDescriptorProvider()
    {
        return getServerContext().getExecutorServiceDescriptorProvider();
    }

    @Override
    public IBuildDescriptorProvider getBuildDescriptorProvider()
    {
        return getServerContext().getBuildDescriptorProvider();
    }

    @Override
    public IPropertiesResolver getPropertiesResolver()
    {
        return getServerContext().getPropertiesResolver();
    }

    @Override
    public AuthorizationResult isAuthorized(Object target, JSONObject principals)
    {
        return getServerContext().isAuthorized(target, principals);
    }

    @Override
    public IPubSubDescriptorProvider getPubSubDescriptorProvider()
    {
        return getServerContext().getPubSubDescriptorProvider();
    }

    @Override
    public JSONObject publish(String name, PubSubChannelType type, JSONObject message) throws Exception
    {
        return getServerContext().publish(name, type, message);
    }

    @Override
    public IPubSubHandlerRegistration addMessageReceivedHandler(String name, PubSubChannelType type, Closure<JSONObject> handler) throws Exception
    {
        return getServerContext().addMessageReceivedHandler(name, type, handler);
    }

    @Override
    public IPubSubHandlerRegistration addMessageReceivedHandler(String name, PubSubChannelType type, IPubSubMessageReceivedHandler handler) throws Exception
    {
        return getServerContext().addMessageReceivedHandler(name, type, handler);
    }

    @Override
    public IPubSubHandlerRegistration addStateChangedHandler(String name, PubSubChannelType type, Closure<PubSubStateType> handler) throws Exception
    {
        return getServerContext().addStateChangedHandler(name, type, handler);
    }

    @Override
    public IPubSubHandlerRegistration addStateChangedHandler(String name, PubSubChannelType type, IPubSubStateChangedHandler handler) throws Exception
    {
        return getServerContext().addStateChangedHandler(name, type, handler);
    }

    @Override
    public Logger logger()
    {
        return getServerContext().logger();
    }

    @Override
    public JSONObject json()
    {
        return getServerContext().json();
    }

    @Override
    public JSONObject json(Map<String, ?> valu)
    {
        return getServerContext().json(valu);
    }

    @Override
    public JSONObject json(String name, Object value)
    {
        return getServerContext().json(name, value);
    }

    @Override
    public JSONObject json(Collection<?> collection)
    {
        return getServerContext().json(collection);
    }

    @Override
    public JSONObject json(List<?> list)
    {
        return getServerContext().json(list);
    }
}
