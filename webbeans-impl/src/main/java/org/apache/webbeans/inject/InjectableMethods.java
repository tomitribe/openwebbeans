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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;

import org.apache.webbeans.annotation.CurrentLiteral;
import org.apache.webbeans.component.AbstractComponent;
import org.apache.webbeans.component.ComponentImpl;
import org.apache.webbeans.component.ObservesMethodsOwner;
import org.apache.webbeans.component.ProducerComponentImpl;
import org.apache.webbeans.exception.WebBeansException;
import org.apache.webbeans.util.AnnotationUtil;

@SuppressWarnings("unchecked")
public class InjectableMethods<T> extends AbstractInjectable
{
    /** Injectable method */
    protected Method m;

    /** Component instance that owns the method */
    protected Object instance;

    /**
     * Constructs new instance.
     * 
     * @param m injectable method
     * @param instance component instance
     */
    public InjectableMethods(Method m, Object instance, AbstractComponent<?> owner,CreationalContext<?> creationalContext)
    {
        super(owner,creationalContext);
        this.m = m;
        this.instance = instance;
        this.injectionMember = m;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.webbeans.inject.Injectable#doInjection()
     */
    public T doInjection()
    {
        Type[] types = m.getGenericParameterTypes();
        List<Object> list = new ArrayList<Object>();
        
        Annotation[] methodAnnots = m.getDeclaredAnnotations();
        
        this.injectionAnnotations = methodAnnots;
        
        if (isResource(methodAnnots))
        {
            // if the method itself is resource annotated, e.g. @PersistenceUnit
            list.add(inject(types[0], methodAnnots));
        }
        else 
        {
            // otherwise we inject the method parameters as usual
            Annotation[][] annots = m.getParameterAnnotations();
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
    
                    Annotation anns[] = AnnotationUtil.getBindingAnnotations(annot);                                        
                    
                    //check producer component for @Disposes,@Observes via @Realizes
                    Annotation[] fromRealizes = configureRealizesDisposeOrObserves(annot, anns);
                    
                    if(fromRealizes != null && fromRealizes.length > 0)
                    {
                        anns = fromRealizes;
                    }
                                         
                    list.add(inject(type, anns));
    
                    i++;
    
                }
    
            }
        }
        
        try
        {
            if (!m.isAccessible())
            {
                m.setAccessible(true);
            }

            return (T) m.invoke(instance, list.toArray());

        }
        catch (Exception e)
        {
            throw new WebBeansException(e);
        }
    }
    
    private Annotation[] configureRealizesDisposeOrObserves(Annotation[] annot, Annotation[] anns)
    {
        Annotation[] setAnnots = null;
        Class<?> clazz = null;
        boolean isDefined = false;
        //Disposes annotations from the @Realizations
        if(AnnotationUtil.isAnnotationExist(annot, Disposes.class))
        {                        
            if(getInjectionOwnerComponent() instanceof ProducerComponentImpl)
            {
                ProducerComponentImpl<?> producerComponent = (ProducerComponentImpl<?>)getInjectionOwnerComponent();
                if(producerComponent.isFromRealizes())
                {
                     isDefined = true;
                     clazz = producerComponent.getParent().getReturnType();
                }
                
            }                        
        }
        else if(AnnotationUtil.isAnnotationExist(annot, Observes.class))
        {
            if(getInjectionOwnerComponent() instanceof ObservesMethodsOwner)
            {
                ComponentImpl<?> owner = (ComponentImpl<?>)getInjectionOwnerComponent();
                if(owner.isFromRealizes())
                {
                    isDefined = true;
                    clazz = owner.getReturnType();
                }
            }
            
        }
        
        if(isDefined)
        {
            setAnnots = AnnotationUtil.getRealizesGenericAnnotations(clazz, anns);
        }

        return setAnnots;        
    }
}