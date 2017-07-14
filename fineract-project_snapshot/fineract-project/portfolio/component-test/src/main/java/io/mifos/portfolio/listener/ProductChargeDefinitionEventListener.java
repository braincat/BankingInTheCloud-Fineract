/*
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.portfolio.listener;

import io.mifos.portfolio.api.v1.events.ChargeDefinitionEvent;
import io.mifos.portfolio.api.v1.events.EventConstants;
import io.mifos.core.lang.config.TenantHeaderFilter;
import io.mifos.core.test.listener.EventRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
@Component
public class ProductChargeDefinitionEventListener {

  private final EventRecorder eventRecorder;

  @Autowired
  public ProductChargeDefinitionEventListener(final EventRecorder eventRecorder) {
    this.eventRecorder = eventRecorder;
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_POST_CHARGE_DEFINITION
  )
  public void onCreateProductChargeDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                     final String payload) {
    this.eventRecorder.event(tenant, EventConstants.POST_CHARGE_DEFINITION, payload, ChargeDefinitionEvent.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_PUT_CHARGE_DEFINITION
  )
  public void onChangeProductChargeDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                     final String payload) {
    this.eventRecorder.event(tenant, EventConstants.PUT_CHARGE_DEFINITION, payload, ChargeDefinitionEvent.class);
  }

  @JmsListener(
          subscription = EventConstants.DESTINATION,
          destination = EventConstants.DESTINATION,
          selector = EventConstants.SELECTOR_DELETE_PRODUCT_CHARGE_DEFINITION
  )
  public void onDeleteProductChargeDefinition(@Header(TenantHeaderFilter.TENANT_HEADER) final String tenant,
                                     final String payload) {
    this.eventRecorder.event(tenant, EventConstants.DELETE_PRODUCT_CHARGE_DEFINITION, payload, ChargeDefinitionEvent.class);
  }
}
