/*
 * Copyright (c) ACCA Corp.
 * All Rights Reserved.
 */
package com.pps.movie.pachong.test;

import com.pps.http.myrequest.phantom.PhantomClientHttpRequest;
import com.pps.http.myrequest.phantom.PhantomClientHttpResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.util.Arrays;
import java.util.List;

/**
 * @author Pu PanSheng, 2021/5/14
 * @version OPRA v1.0
 */
public class PhantomDriverUtil {


    private static final ThreadLocal<PhantomJSDriver> PHANTOM_JS_DRIVER_THREAD_LOCAL=new ThreadLocal<>();

    public static void initDriver(PhantomJSDriver phantomJSDriver){
        PHANTOM_JS_DRIVER_THREAD_LOCAL.set(phantomJSDriver);
    }
    public static void removeDriver(PhantomJSDriver phantomJSDriver){
        PHANTOM_JS_DRIVER_THREAD_LOCAL.remove();
    }
    public static PhantomJSDriver  getDriver(){
        return PHANTOM_JS_DRIVER_THREAD_LOCAL.get();
    }
    public  static PhantomClientHttpResponse castResponse(Object object){
        if(object instanceof  PhantomClientHttpResponse){
            return (PhantomClientHttpResponse)object;
        }
        throw new UnsupportedOperationException(object.getClass().getName()+": 该类型无法强制转换为：PhantomClientHttpResponse");
    }


    public static PhantomClientHttpRequest castRequest(Object object){
        if(object instanceof  PhantomClientHttpRequest){
            return (PhantomClientHttpRequest) object;
        }
        throw new UnsupportedOperationException(object.getClass().getName()+": 该类型无法强制转换为：PhantomClientHttpRequest");
    }

    /**
     * 开启控制台打印所有日志 包括源网页打印的日志 这个只能只能在打开网页动作之前才起作用
     */
    public static void openConsoleLog(){
     PHANTOM_JS_DRIVER_THREAD_LOCAL.get().executePhantomJS("var page=this;" +
                "page.onConsoleMessage = function(msg, lineNum, sourceId) {" +
                "  console.log('CONSOLE: ' + msg + ' (from line #' + lineNum + ' in \"' + sourceId + '\")');\n" +
                "};");
    }

    /**
     * 关于script  这个脚本是在phantomjs 中执行的 可以获取到页面元素 但是无法得到页面的那些变量（取决去执行时机 若是在请求前就不行）
     * @param script
     */
    public static void evaluateScript(String script){

        String js="var page=this; page.evaluate(function() {" +
                "    %s" +
                "  });";
        PHANTOM_JS_DRIVER_THREAD_LOCAL.get().executePhantomJS(String.format(js,script));

    }

    /**
     * 生成存储在页面数据的方法;
     * @return
     */
    public static String generateDataToPageJavascipt(){

        String js=";var saveData=function(data){var ele=document.getElementById('DATA-PPS-PHANTOMJS');" +
                "if(ele==null){" +
                "ele=document.createElement('textarea');" +
                "ele.setAttribute(\"id\",\"DATA-PPS-PHANTOMJS\");" +
                "ele.value=data;" +
                "document.body.appendChild(ele);" +
                "}else{" +
                "ele.value=ele.value+\"####\"+data;" +
                "}};";
        return  js;

    }

    /**
     * 获得自己储存在页面上的内容 若没有则返回null
     * @return
     */
    public static List<String> getDataFromPage(){

        WebElement element = PHANTOM_JS_DRIVER_THREAD_LOCAL.get().findElement(By.id("DATA-PPS-PHANTOMJS"));
        String text = element.getAttribute("value");
        if(text!=null){
            String[] split = text.split("####");
            return Arrays.asList(split);
        }

        return null;
    }

    /**
     * 获得网站的地址
     * @return
     */
    public static String getHostAddressScript(){
       return "var hostAddress=window.location.protocol+'//'+window.location.host+window.location.port;";
    }
}
