/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.action.job;

import com.carrotsearch.hppc.cursors.IntCursor;
import io.crate.executor.transport.TransportExecutor;
import io.crate.operation.NodeOperation;
import io.crate.operation.NodeOperationTree;
import io.crate.planner.Plan;
import io.crate.testing.SQLExecutor;
import org.elasticsearch.test.cluster.NoopClusterService;
import org.openjdk.jmh.annotations.*;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 5)
@Measurement(iterations = 10)
@State(Scope.Benchmark)
public class NodeOperationCtxBenchmark {

    private Collection<NodeOperation> nodeOperations;

    @Setup
    public void setupNodeOperations() {
        SQLExecutor e = SQLExecutor.builder(new NoopClusterService()).build();
        Plan plan = e.plan("select name from sys.cluster group by name");

        TransportExecutor.NodeOperationTreeGenerator nodeOpGenerator = new TransportExecutor.NodeOperationTreeGenerator();
        NodeOperationTree nodeOperationTree = nodeOpGenerator.fromPlan(plan, "noop_id");
        nodeOperations = nodeOperationTree.nodeOperations();
    }

    @Benchmark
    public Iterable<? extends IntCursor> measureCreateNodeOperationCtx() {
        ContextPreparer.NodeOperationCtx ctx = new ContextPreparer.NodeOperationCtx("noop_id", nodeOperations);
        return ctx.findLeafs();
    }
}