/**
 * 
 */
package ngat.astrometry.components;

import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author eng
 *
 */
public class RotatorMountTimePanel2BeanInfo extends SimpleBeanInfo {
	
	  private static final int defaultPropertyIndex = -1;
      private static final int defaultEventIndex = -1;

      public BeanDescriptor getBeanDescriptor() {
              BeanDescriptor beanDescriptor = new BeanDescriptor(RotatorMountTimePanel2.class, null);

              return beanDescriptor;
      }

      public PropertyDescriptor[] getPropertyDescriptors() {
              PropertyDescriptor[] properties = new PropertyDescriptor[2];

              try {
                      properties[0] = new PropertyDescriptor("instOffset", RotatorMountTimePanel2.class, null, "setColor1");
                      properties[1] = new PropertyDescriptor("rotatorMode", RotatorMountTimePanel2.class, null, "setInstOffset");
              } catch (IntrospectionException e) {
                      e.printStackTrace();
              }

              return properties;
      }

      public EventSetDescriptor[] getEventSetDescriptors() {
              EventSetDescriptor[] eventSets = new EventSetDescriptor[0];

              return eventSets;
      }

      public MethodDescriptor[] getMethodDescriptors() {
              MethodDescriptor[] methods = new MethodDescriptor[0];

              return methods;
      }

      public int getDefaultPropertyIndex() {
              return defaultPropertyIndex;
      }

      public int getDefaultEventIndex() {
              return defaultEventIndex;
      }

	
}
