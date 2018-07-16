package pl.kogx.netflixdb.service.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class NullAwareBeanUtilsBean extends BeanUtilsBean {

    @Override
    public void copyProperties(Object dest, Object orig) {
        try {
            super.copyProperties(dest, orig);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void copyProperty(Object dest, String name, Object value) throws InvocationTargetException, IllegalAccessException {
        if (value == null) {
            return;
        }
        super.copyProperty(dest, name, value);
    }
}
