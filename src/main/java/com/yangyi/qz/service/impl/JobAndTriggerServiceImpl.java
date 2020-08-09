package com.yangyi.qz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yangyi.qz.dao.JobAndTriggerMapper;
import com.yangyi.qz.domain.dto.JobAndTriggerDTO;
import com.yangyi.qz.domain.vo.BaseVO;
import com.yangyi.qz.exception.DeleteException;
import com.yangyi.qz.exception.FindException;
import com.yangyi.qz.exception.InsertException;
import com.yangyi.qz.exception.UpdateException;
import com.yangyi.qz.service.JobAndTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/25
 */
@Slf4j
@Service("jobAndTriggerService")
public class JobAndTriggerServiceImpl implements JobAndTriggerService {

    @Qualifier("jobAndTriggerMapper")
    @Autowired
    private JobAndTriggerMapper jobAndTriggerMapper;

    @Autowired
    private Scheduler scheduler;

    /**
     * 启动调度器
     */
    @PostConstruct
    public void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("Quartz scheduler 任务调度器初始化失败！");
        }
    }

    @Override
    public PageInfo<JobAndTriggerDTO> getJobAndTriggerDetails(BaseVO vo) {
        PageHelper.startPage(vo.getPageIndex(), vo.getPageSize());
        List<JobAndTriggerDTO> list = jobAndTriggerMapper.getJobAndTriggerDetails();
        PageInfo<JobAndTriggerDTO> pageInfo = new PageInfo<>(list);
        PageHelper.clearPage();
        return pageInfo;
    }

    /**
     * 增加一个job
     *
     * @param jobClassName
     *            任务实现类
     * @param jobName
     *            任务名称
     * @param jobGroupName
     *            任务组名
     * @param bufferTimeSeconds
     *            运行时间间隔 (每隔多少秒执行一次任务)
     * @param repeatCount
     *            重新运行的次数 0：表示一共运行1次，小于0:表示不限次数，大于0：表示运行 n+1 次
     * @param params
     *            参数
     */
    @Override
    public Boolean addJob(String jobClassName, String jobName, String jobGroupName, Integer bufferTimeSeconds,
                          Integer repeatCount, Map<String, Object> params) {

        checkJob(jobName,jobGroupName);

        String msg = null;
        try {
            // 判断是否已存在
            Boolean existed = isExisted(jobName, jobGroupName);
            if (existed) {
                throw new InsertException("此定时任务已存在，请不要重复添加");
            }

            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob(getJobClass(jobClassName).getClass()).withIdentity(jobName, jobGroupName).build();
            // 设置job参数
            if(params!= null && params.size()>0){
                jobDetail.getJobDataMap().putAll(params);
            }

            Trigger trigger = null;
            if (repeatCount < 0) {
                // 重复次数为负数表示一直执行
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(bufferTimeSeconds).repeatForever())
                        .startNow().build();
            } else {
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(bufferTimeSeconds).withRepeatCount(repeatCount))
                        .startNow().build();
            }

            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobClassName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobClassName, e.getMessage());
            throw new InsertException(msg);
        }

        return true;
    }

    /**
     * 增加一个job，通过表达式来规定执行规则
     *
     * @param jobClassName
     *            任务实现类
     * @param jobName
     *            任务名称(建议唯一)
     * @param jobGroupName
     *            任务组名
     * @param cronExpression
     *            时间表达式 （如：0/5 * * * * ? ）
     * @param params
     *            参数
     */
    @Override
    public Boolean addJob(String jobClassName, String jobName, String jobGroupName, String cronExpression, Map<String, Object> params) {

        checkJob(jobName,jobGroupName);

        String msg = null;

        try {
            // 判断是否已存在
            Boolean existed = isExisted(jobName, jobGroupName);
            if (existed) {
                throw new InsertException("此定时任务已存在，请不要重复添加");
            }

            //构建job信息
            JobDetail jobDetail = JobBuilder
                    .newJob(getJobClass(jobClassName).getClass())
                    .withIdentity(jobName, jobGroupName).build();

            // 设置job参数
            if(params!= null && params.size()>0){
                jobDetail.getJobDataMap().putAll(params);
            }

            //定义调度触发规则，绑定触发表达式
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            // 使用cornTrigger规则
            // 触发器key
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                    .withSchedule(scheduleBuilder).build();

            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (ObjectAlreadyExistsException e) {
            msg = "任务调度失败，"+ jobClassName +" 任务已存在";
            e.printStackTrace();

            log.error("任务调度失败，{} 任务已存在", jobClassName);
            throw new InsertException(msg);
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobClassName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobClassName, e.getMessage());
            throw new InsertException(msg);
        }

        return true;
    }

    /**
     * 暂停一个job
     * @param jobName 任务名称
     * @param jobGroupName 任务所在的组
     */
    @Override
    public Boolean jobPause(String jobName, String jobGroupName) {
        checkJob(jobName,jobGroupName);
        String msg = null;
        try {
            // 通过SchedulerFactory获取一个调度器实例
            scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobName, e.getMessage());
            throw new InsertException(msg);
        }
        return true;
    }

    /**
     * 恢复一个job
     * @param jobName 任务名称
     * @param jobGroupName 任务所在的组
     */
    @Override
    public Boolean jobResume(String jobName, String jobGroupName) {

        checkJob(jobName,jobGroupName);

        String msg = null;
        try {
            // 通过SchedulerFactory获取一个调度器实例
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobName, e.getMessage());
            throw new InsertException(msg);
        }
        return true;
    }

    /**
     *  根据条件更新定时任务
     * @param jobName   任务名称
     * @param jobGroupName   任务所在的组
     * @param cronExpression 新的触发表达式
     * @return
     */
    @Override
    public Boolean updateJob(String jobName, String jobGroupName, String cronExpression) {

        checkJob(jobName,jobGroupName);

        String msg = null;
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            if (trigger == null) {
                throw new UpdateException("定时任务不存在或已被删除");
            }

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobName, e.getMessage());
            throw new UpdateException(msg);
        }
        return true;
    }

    /**
     * 根据条件删除定时任务
     * @param jobName   任务名称
     * @param jobGroupName 任务所在的组
     * @return
     */
    @Transactional
    @Override
    public Boolean deleteJob(String jobName, String jobGroupName) {

        checkJob(jobName,jobGroupName);

        String msg = null;
        try {
            //停止触发器
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
            //移除触发器
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
            //删除任务
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobName, e.getMessage());
            throw new DeleteException(msg);
        }
        return true;
    }

    /**
     * 立即执行一个job
     *
     * @param jobName   任务名称
     * @param jobGroupName 任务所在的组
     */
    @Override
    public Boolean runAJobNow(String jobName, String jobGroupName) {

        checkJob(jobName,jobGroupName);

        String msg = null;
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            msg = "任务调度失败，"+ jobName +" 无法正常执行调度命令，请联系管理员处理";
            e.printStackTrace();

            log.error("任务调度失败，{} 无法正常执行调度命令，异常信息：{}", jobName, e.getMessage());
            throw new UpdateException(msg);
        }
        return true;
    }

    /**
     * 获取所有计划中的任务列表
     * @return List<Map<String, Object>>
     */
    @Override
    public List<Map<String, Object>> queryAllJob() {
        List<Map<String, Object>> jobList = null;
        String msg = null;
        try {
            // 获取所有的 job
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);

            jobList = new ArrayList<>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobKey.getName());
                    map.put("jobGroupName", jobKey.getGroup());
                    map.put("description", "触发器:" + trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    map.put("jobStatus", triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        map.put("jobTime", cronExpression);
                    }
                    jobList.add(map);
                }
            }
        } catch (SchedulerException e) {
            msg = "任务查询失败，请稍后重试";
            e.printStackTrace();

            log.error("任务查询失败，异常信息：{}", e.getMessage());
            throw new UpdateException(msg);
        }
        return jobList;
    }

    /**
     * 获取所有正在运行的job
     *
     * @return List<Map<String, Object>>
     */
    @Override
    public List<Map<String, Object>> queryRunJob() {
        List<Map<String, Object>> jobList = null;
        String msg = null;
        try {
            // 获取所有在正执行的的 job
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();

            jobList = new ArrayList<>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                Map<String, Object> map = new HashMap<>();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                map.put("jobName", jobKey.getName());
                map.put("jobGroupName", jobKey.getGroup());
                map.put("description", "触发器:" + trigger.getKey());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                map.put("jobStatus", triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    map.put("jobTime", cronExpression);
                }
                jobList.add(map);
            }
        } catch (SchedulerException e) {
            msg = "任务查询失败，请稍后重试";
            e.printStackTrace();

            log.error("任务查询失败，异常信息：{}", e.getMessage());
            throw new UpdateException(msg);
        }
        return jobList;
    }

    /**
     * 暂停所有任务
     * @return 操作成功返回true，否则返回 false
     */
    @Override
    public Boolean pauseAllJob() {
        String msg;
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            msg = "暂停所有任务失败，请稍后重试";
            e.printStackTrace();

            log.error("暂停所有任务失败，异常信息：{}", e.getMessage());
            throw new UpdateException(msg);
        }
        return true;
    }

    /**
     * 恢复所有任务
     * @return 操作成功返回true，否则返回 false
     */
    @Override
    public Boolean resumeAllJob() {
        String msg;
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            msg = "恢复所有任务失败，请稍后重试";
            e.printStackTrace();

            log.error("恢复所有任务失败，异常信息：{}", e.getMessage());
            throw new UpdateException(msg);
        }
        return true;
    }

    /**
     * 检查任务信息
     * @param jobName job 名称
     * @param jobGroupName job 所在的组
     */
    private void checkJob(String jobName, String jobGroupName) {
        if (StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName)) {
            throw new InsertException("任务名称和任务所属的组不能为空");
        }
    }

    /**
     * 判断定时任务是否存在
     * @param jobName job 名称
     * @param jobGroupName job 所在的组
     */
    @Override
    public Boolean isExisted(String jobName, String jobGroupName){
        checkJob(jobName, jobGroupName);
        //获得触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        Trigger trigger = null;
        try {
            trigger = scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("定时任务查询异常，异常信息：{}", e.getMessage());
            throw new FindException("定时任务查询异常，异常信息：" + e.getMessage());
        }
        return trigger != null;
    }

    /**
     * 获取 job 实例
     * @param jobClassName 类名
     * @return QuartzJobBean
     */
    private static QuartzJobBean getJobClass(String jobClassName){
        Class<?> clazz = null;
        QuartzJobBean quartzJobBean = null;
        String msg = null;
        try {
            clazz = Class.forName(jobClassName);
            quartzJobBean = (QuartzJobBean) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            msg = "job创建失败，"+ jobClassName +" 该job类不存在或已被删除";
            e.printStackTrace();

            log.warn("job创建失败，{} 该类不存在或已被删除", jobClassName);
            throw new InsertException(msg);
        } catch (InstantiationException | IllegalAccessException e) {
            msg = "job创建失败，"+ jobClassName +" 无法创建该job类的实例，请检查配置是否正确";
            e.printStackTrace();

            log.error("job创建失败，{} 无法创建该job类的实例，请检查配置是否正确", jobClassName);
            throw new InsertException(msg);
        }
        return quartzJobBean;
    }
}
