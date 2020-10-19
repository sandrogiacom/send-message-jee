package com.identity.broker.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fluig.broker.BrokerClient;
import com.fluig.broker.controller.ChannelController;
import com.fluig.broker.domain.BrokerResponse;
import com.fluig.broker.domain.ConnectionVO;
import com.fluig.broker.domain.builder.BrokerRequestBuilder;
import com.fluig.broker.domain.builder.BrokerRequestHeaderBuilder;
import com.fluig.broker.domain.builder.ChannelControllerDTOBuilder;
import com.fluig.broker.domain.builder.ConnectionVOBuilder;
import com.fluig.broker.exception.BrokerException;
import com.identity.broker.message.dto.AdGroupIdentityCreateConfirmationDTO;
import com.identity.broker.message.dto.SyncEventDTO;
import com.identity.broker.message.enums.AdGroupStatus;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;

public class SendGroupConfirmationMessage {

    public static final String AD_GROUP_ID = "33cb6dd8fcdc4ba5a8c00d6d644277e2";
    public static final String COMPANY_ID = "labs0da4110ba377d100f050cb4440db";
    public static final String OBJECT_GUID = "6de78c06cc3d45a4bba044f9776f41d2";
    public BrokerClient brokerClient;

    @BeforeEach
    public void init() {
        brokerClient = getBrokerClient();
    }

    private BrokerClient getBrokerClient() {
        ConnectionVO vo = ConnectionVOBuilder.of()
                .build();
        brokerClient = new BrokerClient(vo);
        return brokerClient;
    }

    @Test
    public void sendGroupConfirmation() throws BrokerException {
        SyncEventDTO sync = new SyncEventDTO();
        sync.setType("IDM_GROUP_CREATE_CONFIRMATION");
        sync.setActiveDirectoryId(AdConstants.ACTIVE_DIRECTORY_ID);
        sync.setMessage(buildGroupConformation(AdGroupStatus.ACCEPTED));

        ChannelController topicController;

        topicController = brokerClient.createTopicChannel(
                ChannelControllerDTOBuilder.of()
                        .exchangeName(BrokerConstants.IDM_AD_SYNC_IDENTITY_TOPIC)
                        .routingKey(BrokerConstants.IDM_AD_SYNC_IDENTITY_RETURN_QUEUE)
                        .queueName(BrokerConstants.IDM_AD_SYNC_IDENTITY_RETURN_QUEUE)
                        .build()
        );

        BrokerResponse response = topicController.sendMessage(BrokerRequestBuilder.of()
                        .header(BrokerRequestHeaderBuilder.of()
                                .type(BrokerConstants.IDM_AD_SYNC_IDENTITY_TOPIC)
                                .build())
                        .body(sync)
                        .build(),
                getProperties());

        System.out.println(response);

    }

    private AMQP.BasicProperties getProperties() {
        return MessageProperties.MINIMAL_PERSISTENT_BASIC.builder().contentType("application/json").build();
    }

    private AdGroupIdentityCreateConfirmationDTO buildGroupConformation(AdGroupStatus status) {
        AdGroupIdentityCreateConfirmationDTO confirmationDTO = new AdGroupIdentityCreateConfirmationDTO();
        confirmationDTO.setAdGroupId(AD_GROUP_ID);
        confirmationDTO.setCompanyId(COMPANY_ID);
        confirmationDTO.setIdmGroupId("12345");
        confirmationDTO.setObjectGuid(OBJECT_GUID);
        confirmationDTO.setStatus(status);
        if (status.equals(AdGroupStatus.REJECTED)) {
            confirmationDTO.setErrorMessage("Company Id not found");
        }
        return confirmationDTO;
    }
}
