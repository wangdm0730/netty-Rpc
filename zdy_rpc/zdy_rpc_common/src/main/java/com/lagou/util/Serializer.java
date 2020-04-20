package com.lagou.util;

import java.io.IOException;

/**
 * @author wangdm
 * @description 序列化
 */
public interface Serializer {

    /**

     * java对象转换为二进制

     *

     * @param object

     * @return

     */

    byte[] serialize(Object object) throws IOException;



    /**

     * 二进制转换成java对象

     *

     * @param clazz

     * @param bytes

     * @param <T>

     * @return

     */

    <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException;

}
