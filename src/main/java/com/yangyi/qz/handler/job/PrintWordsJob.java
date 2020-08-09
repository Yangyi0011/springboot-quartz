package com.yangyi.qz.handler.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/23
 */
public class PrintWordsJob extends QuartzJobBean {
    public static volatile AtomicInteger cnt = new AtomicInteger(0);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 此处可以提取出携带过来的任务参数
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Object name = jobDataMap.get("name");

        String printTime = new SimpleDateFormat("yy-MM-dd HH-mm-ss").format(new Date());
        System.out.println("PrintWordsJob start at:" + printTime + ", prints: Hello Job-" + cnt.incrementAndGet());
        System.out.println("my name is " + name);
    }
}
