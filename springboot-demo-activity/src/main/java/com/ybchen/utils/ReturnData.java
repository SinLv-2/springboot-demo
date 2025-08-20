package com.ybchen.utils;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description: 统一协议JsonData工具类
 * @author: Alex
 * @create: 2023-08-16 22:29
 */
public class ReturnData<T> implements Serializable {
    /**
     * 状态码 0 表示成功，1表示处理中，-1表示失败
     */
    private Integer code;
    /**
     * 数据
     */
    private T data;
    /**
     * 描述
     */
    private String msg;

    private ReturnData() {
    }

    private static <T> ReturnData<T> build(Integer code, T data, String msg) {
        ReturnData<T> json = new ReturnData();
        json.code = code;
        json.data = data;
        json.msg = msg;
        return json;
    }

    /**
     * 成功，传⼊数据
     *
     * @return
     */
    public static <T> ReturnData<T> buildSuccess() {
        return build(0, null, "");
    }


    /**
     * 成功，传⼊数据
     *
     * @param data
     * @return
     */
    public static <T> ReturnData<T> buildSuccess(T data) {
        return build(0, data, "");
    }

    /**
     * 失败，传⼊描述信息
     *
     * @param msg
     * @return
     */
    public static <T> ReturnData<T> buildError(String msg) {
        return build(-1, null, msg);
    }

    /**
     * ⾃定义状态码和错误信息
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> ReturnData<T> buildCodeAndMsg(int code, String msg) {
        return build(code, null, msg);
    }

    /**
     * ⾃定义状态码和错误信息
     *
     * @param bizCode    二级业务状态码
     * @param bizContent 二级业务响应内容
     * @return
     */
    public static <T> ReturnData<T> buildBizCodeAndMsg(int bizCode, Object bizContent) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("biz_code", bizCode);
        map.put("biz_content", bizContent);
        return build(0, (T) map, null);
    }

    /**
     * 判断接口响应是否成功，只是判断状态码是否等于：0
     *
     * @param data
     * @return
     */
    public static boolean isSuccess(ReturnData data) {
        return data.getCode() == 0;
    }

    /**
     * 判断接口响应是否失败，状态码除了0以外的，默认调用失败
     *
     * @param data
     * @return
     */
    public static boolean isFailure(ReturnData data) {
        return !isSuccess(data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        Map<String, Object> resultMap = new LinkedHashMap<>(3);
        resultMap.put("code", code);
        resultMap.put("data", data);
        resultMap.put("msg", msg);
        return JSON.toJSONString(resultMap);
    }
}