package com.nelson.viewinject_api;

/**
 * Created by Nelson on 17/4/26.
 */

public interface ViewInject<T> {

    void inject(T t, Object source);
}
