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
package io.mifos.accounting.api.v1;

@SuppressWarnings("unused")
public interface PermittableGroupIds {

  String THOTH_LEDGER = "accounting__v1__ledger";
  String THOTH_ACCOUNT = "accounting__v1__account";
  String THOTH_JOURNAL = "accounting__v1__journal";
  String THOTH_TX_TYPES = "accounting__v1__tx_types";

}
