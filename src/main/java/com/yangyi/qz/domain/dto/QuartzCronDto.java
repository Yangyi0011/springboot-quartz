package com.yangyi.qz.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/26
 */
@ApiModel(description = "告警信息详情DTO")
@Data
@Accessors(chain = true)
public class QuartzCronDto {

    @NotBlank(message = "任务全类名不能为空")
    @ApiModelProperty(value = "任务全类名",name = "jobClassName", example = "com.yangyi.qz.handler.job.PrintWordsJob")
    private String jobClassName;

    @NotBlank(message = "任务名称不能为空")
    @ApiModelProperty(value = "任务名称",name = "jobName", example = "testJob")
    private String jobName;

    @NotBlank(message = "任务组名称不能为空")
    @ApiModelProperty(value = "任务组名称",name = "jobGroupName", example = "testGroup")
    private String jobGroupName;

    @ApiModelProperty(value = "运行时间间隔（秒），若运行表达式存在，则不会使用时间间隔",name = "bufferTimeSeconds", example = "60")
    private Integer bufferTimeSeconds;

    @ApiModelProperty(value = "运行总次数，负数表示无限次",name = "repeatCount", example = "3")
    private Integer repeatCount;

    @ApiModelProperty(value = "运行表达式",name = "cronExpression", example = "0 */1 * * * ?")
    private String cronExpression;

    @ApiModelProperty(value = "运行任务需要携带的参数",name = "repeatCount", example = "{k1:v1,k2:v2}")
    private Map<String, Object> params;
}
