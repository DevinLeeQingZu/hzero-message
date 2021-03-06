package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.MessageEventDTO;
import org.hzero.message.app.service.MessageEventService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.MessageEvent;
import org.hzero.message.domain.repository.MessageEventRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 消息事件 管理 API
 *
 * @author like.zhang@hand-china.com 2018-09-12 13:59:26
 */
@Api(tags = MessageSwaggerApiConfig.MESSAGE_EVENT_SITE)
@RestController("messageEventSiteController.v1")
@RequestMapping("/v1/message-events")
public class MessageEventSiteController extends BaseController {

    @Autowired
    private MessageEventRepository messageEventRepository;

    @Autowired
    private MessageEventService messageEventService;

    @ApiOperation(value = "消息事件列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eventCode", value = "事件编码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户Id", required = true, paramType = "query")
    })
    @CustomPageRequest
    public ResponseEntity<Page<MessageEventDTO>> pageMessageEvent(String eventCode,
                                                                  @RequestParam("tenantId") Long tenantId,
                                                                  @ApiIgnore @SortDefault(value = MessageEvent.FIELD_EVENT_CODE) PageRequest pageRequest) {
        MessageEventDTO messageEvent = new MessageEventDTO();
        messageEvent.setEventCode(eventCode);
        messageEvent.setTenantId(tenantId);
        Page<MessageEventDTO> page = messageEventRepository.pageMessageEvent(pageRequest, messageEvent);
        return Results.success(page);
    }

    @ApiOperation(value = "创建消息事件")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<MessageEvent> createMessageEvent(@Encrypt @RequestBody MessageEvent messageEvent) {
        validObject(messageEvent);
        messageEvent = messageEventService.createMessageEvent(messageEvent);
        return Results.success(messageEvent);
    }

    @ApiOperation(value = "修改消息事件")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<MessageEvent> updateMessageEvent(@Encrypt @RequestBody MessageEvent messageEvent) {
        messageEvent = messageEventService.updateMessageEvent(messageEvent);
        return Results.success(messageEvent);
    }

    @ApiIgnore
    @ApiOperation(value = "删除消息事件")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{messageEventId}")
    public ResponseEntity removeMessageEventById(@Encrypt @PathVariable Long messageEventId) {
        messageEventService.removeMessageEventById(messageEventId);
        return Results.success();
    }

    @ApiOperation(value = "批量删除消息事件")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/batch-remove")
    public ResponseEntity batchRemove(@Encrypt @RequestBody List<MessageEvent> messageEvents) {
        messageEventService.batchRemove(messageEvents);
        return Results.success();
    }

    @ApiOperation(value = "发送事件消息")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/send-message-event")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "事件编码", name = "eventCode", required = true),
            @ApiImplicitParam(value = "args", name = "map", required = true),
            @ApiImplicitParam(value = "租户id", name = "tenantId", required = true),
            @ApiImplicitParam(value = "发送类型编码列表,为空表示发送所有类型消息", name = "typeCodeList"),
            @ApiImplicitParam(value = "语言编码", name = "lang", required = true)
    })
    public ResponseEntity sendMessageEvent(String eventCode, Map<String, String> map, Long tenantId, List<String> typeCodeList, String lang) {
        messageEventService.sendMessageEvent(eventCode, map, tenantId, typeCodeList, lang);
        return Results.success();
    }
}
