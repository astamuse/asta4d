/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.web.test.form;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;

@Form
public class TestForm {

    @Hidden
    @Max(30)
    Integer id;

    @Input
    @NotBlank
    String data;

    @CascadeFormField
    @Valid
    SubForm subForm;

    @CascadeFormField(containerSelector = "[cascade-ref=subArray-container-@]", arrayLengthField = "subArrayLength")
    @NotEmpty
    @Valid
    SubArray[] subArray;

    @Hidden
    @NotNull
    Integer subArrayLength;

    @CascadeFormField(containerSelector = "[cascade-ref=subArray2-container-@]", arrayLengthField = "subArrayLength2")
    @NotEmpty
    @Valid
    SubArray2[] subArray2;

    @Hidden
    @NotNull
    Integer subArrayLength2;
}