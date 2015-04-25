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

package com.ait.tooling.server.sql.rpc.commands

import groovy.transform.CompileStatic

import org.springframework.stereotype.Service

import com.ait.tooling.json.JSONObject
import com.ait.tooling.server.core.rpc.CoreGroovyCommandSupport
import com.ait.tooling.server.core.rpc.IJSONRequestContext
import com.ait.tooling.server.core.security.AuthorizationType
import com.ait.tooling.server.core.security.Authorized
import com.ait.tooling.server.sql.support.GSQLTrait

@Service
@CompileStatic
@Authorized(AuthorizationType.ADMIN)
public class GetGSQLDescriptorNamesCommand extends CoreGroovyCommandSupport implements GSQLTrait
{
    @Override
    public JSONObject execute(final IJSONRequestContext context, final JSONObject object) throws Exception
    {
        json(getGSQLProvider().keys().collect())
    }
}
