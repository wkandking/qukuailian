package com.example.qukuailian.service;

import com.example.qukuailian.bean.TimeSM4;
import com.example.qukuailian.dao.TimeSM4Mapper;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.SM4.SM4Util;
import com.example.qukuailian.util.SMA;
import com.example.qukuailian.util.Other.DifferentialPrivacyImpl;
import com.example.qukuailian.util.Other.KanonymityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class CommonService {

    @Autowired
    TimeSM4Mapper timeSM4Mapper;
    public Method getEncryptMethod(String algtype) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = null;
        if(algtype.equals("2")){
            clazz = Class.forName(SMA.SM2.getClassName());
        }else if(algtype.equals("4")){
            clazz = Class.forName(SMA.SM4.getClassName());
        }
        Method method = clazz.getMethod("encrypt", new Class[]{String.class, String.class});
        return method;
    }

    public Method getDecryptMethod(String algtype) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = null;
        if(algtype.equals("2")){
            clazz = Class.forName(SMA.SM2.getClassName());
        }else if(algtype.equals("4")){
            clazz = Class.forName(SMA.SM4.getClassName());
        }
        Method method = clazz.getMethod("decrypt", new Class[]{String.class, String.class});
        return method;
    }

    public String getSM3Method(String message) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName(SMA.SM3.getClassName());
        Method method = clazz.getMethod("getHash", new Class[]{String.class});
        String  hashValue = (String) method.invoke(null, new Object[]{message});
        return hashValue;
    }

    public String timeEncryptBySM4(String message, String time) throws Exception {
        TimeSM4 timeSM4 = timeSM4Mapper.selectByPrimaryKey(time);
        String sm4key;
        if(timeSM4 == null){
            timeSM4 = new TimeSM4();
            sm4key = SM4Util.generateKey(time);
            timeSM4.setTstring(time);
            timeSM4.setSm4key(sm4key);
            timeSM4Mapper.insertSelective(timeSM4);
        }else{
            sm4key = timeSM4.getSm4key();
        }
        Class<?> clazz = Class.forName(SMA.SM4.getClassName());
        Method method = clazz.getMethod("encrypt", new Class[]{String.class, String.class});
        String encryptMessage = (String) method.invoke(null, new Object[]{message, sm4key});
        return encryptMessage;
    }

    public String timeDecryptBySM4(String encMessage, String time) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TimeSM4 timeSM4 = timeSM4Mapper.selectByPrimaryKey(time);
        String sm4key;
        if(timeSM4 == null){
            throw new CustomException(120,"time 没有 对应的 sm4key");
        }
        sm4key = timeSM4.getSm4key();
        Class<?> clazz = Class.forName(SMA.SM4.getClassName());
        Method method = clazz.getMethod("decrypt", new Class[]{String.class, String.class});
        String message = (String) method.invoke(null, new Object[]{encMessage, sm4key});
        return message;
    }

    public Double difPrivacy(Double value,Double epsilon){
        Double retvalue = DifferentialPrivacyImpl.getNoisyDigit(value,epsilon);
        return retvalue;
    }

    public String kanonymity(int k,String context){
        String retvalue = KanonymityImpl.kannoy(k,context);
        return retvalue;
    }
}
