/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.webbeans.newtests.profields;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.webbeans.newtests.AbstractUnitTest;
import org.apache.webbeans.newtests.profields.beans.stringproducer.MultipleListProducer;
import org.junit.Test;

public class ProducerFieldPassivationIdTest extends AbstractUnitTest
{
    
    /**
     * Tests the the getID() method of PassivationCapable Producer Field beans are unique if
     * generics are used in the field type.
     */
    @Test
    public void testMultipleListsWithGenerics()
    {
        Collection<Class<?>> beanClasses = new ArrayList<Class<?>>();
        beanClasses.add(MultipleListProducer.class);
        
        
        //Will fail to deploy if we have conflicting IDs
        startContainer(beanClasses);
        
        shutDownContainer();
    }

}