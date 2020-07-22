/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.rrt.lib;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.james.core.Username;
import org.junit.jupiter.api.Test;

public class DomainRewriterTest {
    @Test
    public void rewriteShouldReplaceDomain() throws Exception {
        assertThat(
            new UserRewritter.DomainRewriter()
                .generateUserRewriter("newdomain.com")
                .rewrite(Username.of("toto@olddomain.com")))
            .contains(Username.of("toto@newdomain.com"));
    }

    @Test
    public void rewriteShouldAddDomainWhenNone() throws Exception {
        assertThat(
            new UserRewritter.DomainRewriter()
                .generateUserRewriter("newdomain.com")
                .rewrite(Username.of("toto")))
            .contains(Username.of("toto@newdomain.com"));
    }
}
