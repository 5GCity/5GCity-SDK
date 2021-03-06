/*
 * Copyright 2020 Nextworks s.r.l.
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
package it.nextworks.sdk.exceptions;


/**
 * @version v0.4
 */
public class MalformedElementException extends Exception {


    private static final long serialVersionUID = 1L;

    public MalformedElementException() {
    }

    public MalformedElementException(String message) {
        super(message);
    }

    public MalformedElementException(Throwable cause) {
        super(cause);
    }

    public MalformedElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedElementException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

