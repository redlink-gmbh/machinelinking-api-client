/*
 *  Copyright 2013 MachineLinking S.r.l.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.machinelinking.api.client;

/**
 * The response for the <i>compare</i> method.
 *
 * @see {@link com.machinelinking.api.client.APIClient#compare(ComparisonMethod, String, String)}
 * @author Michele Mostarda (michele@machinelinking.com)
 */
public class CompareResponse implements Response {

    private final int cost;
    private final float similarity;

    public CompareResponse(int cost, float similarity) {
        this.cost = cost;
        this.similarity = similarity;
    }

    public float getSimilarity() {
        return similarity;
    }

    @Override
    public String getLang() {
        return null;
    }

    @Override
    public int getCost() {
        return cost;
    }

}
