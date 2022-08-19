package com.example.demo.function;
import delight.nashornsandbox.*;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

import javax.script.*;
import java.io.FileReader;
import java.util.concurrent.Executors;

public class sandbox_test {
    private static final NashornSandbox sandbox = NashornSandboxes.create();
    static {
        sandbox.setMaxCPUTime(5000);// 设置脚本执行允许的最大CPU时间（以毫秒为单位），超过则会报异常,防止死循环脚本
        sandbox.setMaxMemory(1024 * 1024); //设置JS执行程序线程可以分配的最大内存（以字节为单位），超过会报ScriptMemoryAbuseException错误
        sandbox.allowNoBraces(false); // 是否允许使用大括号
        sandbox.allowLoadFunctions(true); // 是否允许nashorn加载全局函数
        sandbox.setMaxPreparedStatements(30); // because preparing scripts for execution is expensive // LRU初缓存的初始化大小，默认为0
        sandbox.setExecutor(Executors.newFixedThreadPool(4));// 指定执行程序服务，该服务用于在CPU时间运行脚本
    }
    public static void main(String[] args) throws ScriptException {
        String name = "function test(){ while(true){};};test();";
        try {
            System.out.println(sandbox.eval(name));
        }catch (ScriptCPUAbuseException e) {
            System.out.println(1);
        }
        System.out.println(sandbox.eval("var a=1;var b=2;var c=2;function calFunction(a, b, c){return a+b+c;};calFunction(a,b,c)"));

    }
}
