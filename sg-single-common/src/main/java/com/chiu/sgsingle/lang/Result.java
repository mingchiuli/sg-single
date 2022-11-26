package com.chiu.sgsingle.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mingchiuli
 * @create 2021-10-27 3:27 PM
 */
@Data
public class Result<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        return load(200, "操作成功",data); //200为正常，非200为非正常
    }

    private static <T> Result<T> load(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
    public static <T> Result<T> fail(Integer code, String msg, T data) {
        return load(code, msg, data);
    }

    public static <T> Result<T> fail(String msg) {
        return load(400, msg, null);
    }

}
