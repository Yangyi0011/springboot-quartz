package com.yangyi.qz.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/25
 */
@ApiModel(description = "quartz 任务和触发器DTO")
@Data
@Accessors(chain = true)
public class JobAndTriggerDTO {

    @ApiModelProperty(value = "任务名称",name = "jobName", example = "dailyMessageJob")
    private String jobName;

    @ApiModelProperty(value = "任务所在组",name = "jobGroup", example = "dailyMessageJobGroup")
    private String jobGroup;

    @ApiModelProperty(value = "任务类名",name = "jobClassName", example = "com.encdata.iocs.communication.impl.main.handler.quartz.job.DailyMessagePushJob")
    private String jobClassName;

    @ApiModelProperty(value = "触发器名称",name = "triggerName", example = "dailyMessageTrigger")
    private String triggerName;

    @ApiModelProperty(value = "触发器所在组",name = "triggerGroup", example = "dailyMessageTriggerGroup")
    private String triggerGroup;

    @ApiModelProperty(value = "触发表达式",name = "cronExpression", example = "* * * * *")
    private String cronExpression;

    @ApiModelProperty(value = "时区",name = "timeZoneId", example = "GMT+8")
    private String timeZoneId;

    @ApiModelProperty(value = "触发器状态",name = "triggerStart", example = "-")
    private String triggerStart;

    @ApiModelProperty(value = "触发器创建时间",name = "startTime", example = "2020-01-01 00:00:00")
    private String createTime;

    @ApiModelProperty(value = "上次执行时间",name = "startTime", example = "2020-01-01 00:00:00")
    private String prevFireTime;

    @ApiModelProperty(value = "下次执行时间",name = "startTime", example = "2020-01-01 00:00:00")
    private String nextFireTime;
}
