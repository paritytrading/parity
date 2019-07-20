/*
 * Copyright 2014 Parity authors
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
 */
package com.paritytrading.parity.net;

import java.io.IOException;

/**
 * Indicates a protocol error.
 */
public class ProtocolException extends IOException {

    /**
     * Construct an instance with the specified detail message.
     *
     * @param message the detail message
     */
    public ProtocolException(String message) {
        super(message);
    }

    /**
     * Construct an instance with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an instance with the specified cause.
     *
     * @param cause the cause
     */
    public ProtocolException(Throwable cause) {
        super(cause);
    }

}
