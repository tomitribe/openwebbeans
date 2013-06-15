/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.webbeans.newtests.producer;

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.inject.AmbiguousResolutionException;

import junit.framework.Assert;

import org.apache.webbeans.exception.WebBeansConfigurationException;
import org.apache.webbeans.exception.inject.DeploymentException;
import org.apache.webbeans.newtests.AbstractUnitTest;
import org.junit.Test;

public class AmbigousProducerTest extends AbstractUnitTest
{

    @Test
    public void testAmbiguousProducer()
    {
        Collection<String> beanXmls = new ArrayList<String>();
        Collection<Class<?>> beanClasses = new ArrayList<Class<?>>();

        beanClasses.add(ProducerBean.class);
        beanClasses.add(ProducerBean2.class);
        
        try {
            startContainer(beanClasses, beanXmls);
            Assert.fail("Should have thrown AmbiguousResoultionException");
        }
        catch (WebBeansConfigurationException e)
        {
            Assert.assertEquals(DeploymentException.class, e.getCause().getClass());
            Assert.assertEquals(AmbiguousResolutionException.class, e.getCause().getCause().getClass());
        }
        shutDownContainer();       
        
    }
}
