/*
 * Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.render;

/**
 * for performance reason, we use enum to identify the special renderers.
 * 
 * @author e-ryu
 * 
 */
public enum RendererType {
    /**
     * common renderer
     */
    COMMON,

    /**
     * a debug renderer will output the target element to log
     */
    // DEBUG,

    /**
     * a do nothing renderer
     */
    GO_THROUGH,

    /**
     * a renderer which will change the action style of rendering process
     */
    RENDER_ACTION,

    /**
     * a renderer which will handle the case that the specified selector is not found
     */
    ELEMENT_NOT_FOUND_HANDLER
}
