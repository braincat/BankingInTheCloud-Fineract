/**
 * Copyright 2017 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {NgModule} from '@angular/core';
import {LoginComponent} from './login.component';
import {LoginRoutes} from './login.routing';
import {RouterModule} from '@angular/router';
import {FimsSharedModule} from '../../common/common.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {
  MdButtonModule, MdCardModule, MdIconModule, MdInputModule, MdSelectModule,
  MdTooltipModule
} from '@angular/material';
import {CommonModule} from '@angular/common';
import {TranslateModule} from '@ngx-translate/core';
import {CovalentLoadingModule} from '@covalent/core';

@NgModule({
  imports: [
    FimsSharedModule,
    RouterModule.forChild(LoginRoutes),
    TranslateModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MdIconModule,
    MdCardModule,
    MdInputModule,
    MdButtonModule,
    MdSelectModule,
    MdTooltipModule,
    CovalentLoadingModule
  ],
  declarations: [
    LoginComponent
  ]
})
export class LoginModule {}
