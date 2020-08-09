package com.yangyi.qz.controller.v1;

import com.yangyi.qz.controller.validation.ValidationGroup_Add;
import com.yangyi.qz.domain.dto.QuartzCronDto;
import com.yangyi.qz.domain.vo.BaseVO;
import com.yangyi.qz.service.JobAndTriggerService;
import com.yangyi.qz.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author yangyi
 * @date 2020/04/25
 */
@Api(tags = "DatasourceController", description = "定时任务配置控制类")
@RestController
@Slf4j
@RequestMapping("${spring.application.name}/v2/quartzJobController")
public class QuartzJobController {

    @Qualifier("jobAndTriggerService")
    @Autowired
    private JobAndTriggerService jobAndTriggerService;

    @ApiOperation(value= "添加单次立即执行，且没有参数的任务", notes= "添加单次立即执行，且没有参数的任务")
    @RequestMapping(value = "/addJobWithSingle",method = RequestMethod.POST)
    public R addJobWithSingle(@RequestParam("jobClassName") String jobClassName,
                              @RequestParam("jobName") String jobName,
                              @RequestParam("jobGroupName") String jobGroupName){
        jobAndTriggerService.addJob(jobClassName, jobName,jobGroupName, 1, 0, null);
        return R.success("定时任务添加成功") ;
    }

    @ApiOperation(value= "添加一个复杂任务，可以指定其执行的时间间隔和执行总次数，也可以使其按表达式执行",
            notes= "添加一个复杂任务，可以指定其执行的时间间隔和执行总次数，也可以使其按表达式执行")
    @RequestMapping(value = "/addJob", method = RequestMethod.POST)
    public R addJob(@Validated(value = {ValidationGroup_Add.class}) @RequestBody QuartzCronDto dto){

        if (StringUtils.isEmpty(dto.getCronExpression())) {
            jobAndTriggerService.addJob(dto.getJobClassName(), dto.getJobName(), dto.getJobGroupName(),
                    dto.getBufferTimeSeconds(), dto.getRepeatCount(), dto.getParams());
        } else {
            jobAndTriggerService.addJob(dto.getJobClassName(), dto.getJobName(), dto.getJobGroupName(),
                    dto.getCronExpression(), dto.getParams());
        }
        return R.success("定时任务添加成功") ;
    }

    @ApiOperation(value= "暂停一个任务", notes= "暂停一个任务")
    @RequestMapping(value = "/jobPause",method = RequestMethod.POST)
    public R jobPause(@RequestParam("jobName") String jobName,
                      @RequestParam("jobGroupName") String jobGroupName) {

        jobAndTriggerService.jobPause(jobName, jobGroupName);
        return R.success("定时任务暂停成功");
    }

    @ApiOperation(value= "恢复一个异常任务", notes= "恢复一个异常任务")
    @RequestMapping(value = "/jobResume",method = RequestMethod.POST)
    public R jobResume(@RequestParam("jobName") String jobName,
                       @RequestParam("jobGroupName") String jobGroupName) {

        jobAndTriggerService.jobResume(jobName, jobGroupName);
        return R.success("定时任务恢复成功");
    }

    @ApiOperation(value= "更新一个任务", notes= "更新一个任务")
    @RequestMapping(value = "/updateJob",method = RequestMethod.POST)
    public R updateJob(@RequestParam("jobName") String jobName,
                       @RequestParam("jobGroupName") String jobGroupName,
                       @RequestParam("cronExpression") String cronExpression) {

        jobAndTriggerService.updateJob(jobName, jobGroupName, cronExpression);
        return R.success("定时任务更新成功");
    }

    @ApiOperation(value= "删除一个任务", notes= "删除一个任务")
    @RequestMapping(value = "/deleteJob",method = RequestMethod.POST)
    public R deleteJob(@RequestParam("jobName") String jobName,
                       @RequestParam("jobGroupName") String jobGroupName) {

        jobAndTriggerService.deleteJob(jobName, jobGroupName);
        return R.success("定时任务删除成功");
    }

    @ApiOperation(value= "立即执行一个任务", notes= "立即执行一个任务")
    @RequestMapping(value = "/runAJobNow",method = RequestMethod.POST)
    public R runAJobNow(@RequestParam("jobName") String jobName,
                       @RequestParam("jobGroupName") String jobGroupName) {

        jobAndTriggerService.runAJobNow(jobName, jobGroupName);
        return R.success("定时任务执行成功");
    }

    @ApiOperation(value= "获取所有计划中的任务列表", notes= "获取所有计划中的任务列表")
    @RequestMapping(value = "/queryAllJob",method = RequestMethod.GET)
    public R queryAllJob() {

        return R.success(jobAndTriggerService.queryAllJob());
    }

    @ApiOperation(value= "分页查询任务极其触发器的详细情况", notes= "分页查询任务极其触发器的详细情况")
    @RequestMapping(value = "/getJobAndTriggerDetails",method = RequestMethod.GET)
    public R getJobAndTriggerDetails(BaseVO vo){
        return R.success(jobAndTriggerService.getJobAndTriggerDetails(vo));
    }

    @ApiOperation(value= "获取所有正在运行的任务", notes= "获取所有正在运行的任务")
    @RequestMapping(value = "/queryRunJob",method = RequestMethod.GET)
    public R queryRunJob() {

        return R.success(jobAndTriggerService.queryRunJob());
    }

    @ApiOperation(value= "暂停所有任务", notes= "暂停所有任务")
    @RequestMapping(value = "/pauseAllJob",method = RequestMethod.GET)
    public R pauseAllJob() {

        jobAndTriggerService.pauseAllJob();
        return R.success("所有任务已暂停");
    }

    @ApiOperation(value= "恢复所有任务", notes= "恢复所有任务")
    @RequestMapping(value = "/resumeAllJob",method = RequestMethod.GET)
    public R resumeAllJob() {

        jobAndTriggerService.resumeAllJob();
        return R.success("所有任务已恢复");
    }
}
