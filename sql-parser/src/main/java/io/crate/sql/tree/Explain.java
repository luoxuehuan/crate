/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.sql.tree;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Explain
    extends Statement {
    private final Statement statement;
    private final List<ExplainOption> options;

    public Explain(Statement statement, List<ExplainOption> options) {
        this.statement = checkNotNull(statement, "statement is null");
        if (options == null) {
            this.options = ImmutableList.of();
        } else {
            this.options = ImmutableList.copyOf(options);
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public List<ExplainOption> getOptions() {
        return options;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context) {
        return visitor.visitExplain(this, context);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(statement, options);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Explain o = (Explain) obj;
        return Objects.equal(statement, o.statement) &&
               Objects.equal(options, o.options);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("statement", statement)
            .add("options", options)
            .toString();
    }
}
