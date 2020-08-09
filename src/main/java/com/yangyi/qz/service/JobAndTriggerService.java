package com.yangyi.qz.service;


import com.github.pagehelper.PageInfo;
import com.yangyi.qz.domain.dto.JobAndTriggerDTO;
import com.yangyi.qz.domain.vo.BaseVO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/25
 */
public interface JobAndTriggerService {

    /**
     * 查询 quartz 任务和触发器信息
     *
     * @param vo 查询条件 vo
     * @return PageInfo
     */
    PageInfo<JobAndTriggerDTO> getJobAndTriggerDetails(BaseVO vo);

    /**
     * 判断定时任务是否存在
     * @param jobName job 名称
     * @param jobGroupName job 所在的组
     */
    Boolean isExisted(String jobName, String jobGroupName);

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
    Boolean addJob(String jobClassName, String jobName, String jobGroupName, Integer bufferTimeSeconds,
                   Integer repeatCount, Map<String, Object> params);

    /**
     * 增加一个job
     *
     * @param jobClassName
     *            任务实现类
     * @param jobName
     *            任务名称
     * @param jobGroupName
     *            任务组名
     * @param cronExpression
     *            时间表达式 （如：0/5 * * * * ? ）
     * @param params
     *            参数
     */
    Boolean addJob(String jobClassName, String jobName, String jobGroupName, String cronExpression, Map<String, Object> params);

    /**
     * 根据条件暂停定时任务
     *
     * @param jobName 任务名称
     * @param jobGroupName 任务所在的组
     * @return 操作成功返回 true，否则返回 false
     */
    Boolean jobPause(String jobName, String jobGroupName);

    /**
     * 根据条件恢复定时任务
     *
     * @param jobName 任务名称
     * @param jobGroupName 任务所在的组
     * @return 操作成功返回 true，否则返回 false
     */
    Boolean jobResume(String jobName, String jobGroupName);

    /**
     * 根据条件更新定时任务
     *
     * @param jobName   任务名称
     * @param jobGroupName   任务所在的组
     * @param cronExpression 新的触发表达式
     * @return 操作成功返回 true，否则返回 false
     */
    Boolean updateJob(String jobName, String jobGroupName, String cronExpression);

    /**
     * 根据条件删除定时任务
     *
     * @param jobName   任务名称
     * @param jobGroupName 任务所在的组
     * @return 操作成功返回 true，否则返回 false
     */
    Boolean deleteJob(String jobName, String jobGroupName);

    /**
     * 立即执行一个job
     *
     * @param jobName   任务名称
     * @param jobGroupName 任务所在的组
     */
    Boolean runAJobNow(String jobName, String jobGroupName);

    /**
     * 获取所有计划中的任务列表
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> queryAllJob();

    /**
     * 获取所有正在运行的任务
     *
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> queryRunJob();

    /**
     * 暂停所有任务
     * @return 操作成功返回true，否则返回 false
     */
    Boolean pauseAllJob();

    /**
     * 恢复所有任务
     * @return 操作成功返回true，否则返回 false
     */
    Boolean resumeAllJob();
}
