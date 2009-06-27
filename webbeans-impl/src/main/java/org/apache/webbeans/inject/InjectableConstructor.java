/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.webbeans.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.CreationalContext;

import org.apache.webbeans.annotation.CurrentLiteral;
import org.apache.webbeans.component.AbstractComponent;
import org.apache.webbeans.component.ComponentImpl;
import org.apache.webbeans.ejb.EJBUtil;
import org.apache.webbeans.exception.WebBeansException;
import org.apache.webbeans.util.AnnotationUtil;

/**
 * Injects the parameters of the {@link ComponentImpl} constructor and returns
 * the created instance.
 * 
 * @author <a href="mailto:gurkanerdogdu@yahoo.com">Gurkan Erdogdu</a>
 * @since 1.0
 * @see AbstractInjectable
 */
public class InjectableConstructor<T> extends AbstractInjectable
{
    /** Injectable constructor instance */
    protected Constructor<T> con;

    /**
     * Sets the constructor.
     * 
     * @param cons injectable constructor
     */
    public InjectableConstructor(Constructor<T> cons, AbstractComponent<?> owner,CreationalContext<?> creationalContext)
    {
        super(owner,creationalContext);
        this.con = cons;
        this.injectionMember = con;
    }

    /**
     * Creates the instance from the constructor. Each constructor parameter
     * instance is resolved using the resolution algorithm.
     */
    public T doInjection()
    {
        T instance = null;

        Type[] types = con.getGenericParameterTypes();
        Annotation[][] annots = con.getParameterAnnotations();
        List<Object> list = new ArrayList<Object>();
        if (types.length > 0)
        {
            int i = 0;
            for (Type type : types)
            {
                Annotation[] annot = annots[i];
                if (annot.length == 0)
                {
                    annot = new Annotation[1];
                    annot[0] = new CurrentLiteral();
                }

                list.add(inject(type, AnnotationUtil.getBindingAnnotations(annot)));

                i++;

            }

        }

        try
        {
            if (!EJBUtil.isEJBSessionClass(con.getDeclaringClass()))
            {
                if(!con.isAccessible())
                {
                    con.setAccessible(true);
                }
                
                instance = con.newInstance(list.toArray());
            }

        }
        catch (Exception e)
        {
            throw new WebBeansException(e);
        }

        return instance;
    }
}