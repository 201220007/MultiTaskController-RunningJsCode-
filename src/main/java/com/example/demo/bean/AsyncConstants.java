package com.example.demo.bean;

public class AsyncConstants {
    /**
     * 核心线程数
     */
    private Integer corePoolSize = 8;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 16;

    /**
     * 空闲线程存活时间
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 等待队列长度
     */
    private Integer queueCapacity = 100;

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

}
