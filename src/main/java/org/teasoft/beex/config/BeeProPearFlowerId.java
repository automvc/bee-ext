/*
 * Copyright 2016-2021 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.teasoft.beex.config;

/**
 * BeeProPearFlowerId
 * @author Kingstar
 * @since  1.9
 */
public class BeeProPearFlowerId {

	//	prefix = "pearFlowerId_"
	private Long tolerateSecond;
	private Boolean useHalfWorkId;
	private Long switchWorkIdTimeThreshold;
	private Integer randomNumBound;

	public Long getTolerateSecond() {
		return tolerateSecond;
	}

	public void setTolerateSecond(Long tolerateSecond) {
		this.tolerateSecond = tolerateSecond;
	}

	public Boolean getUseHalfWorkId() {
		return useHalfWorkId;
	}

	public void setUseHalfWorkId(Boolean useHalfWorkId) {
		this.useHalfWorkId = useHalfWorkId;
	}

	public Long getSwitchWorkIdTimeThreshold() {
		return switchWorkIdTimeThreshold;
	}

	public void setSwitchWorkIdTimeThreshold(Long switchWorkIdTimeThreshold) {
		this.switchWorkIdTimeThreshold = switchWorkIdTimeThreshold;
	}

	public Integer getRandomNumBound() {
		return randomNumBound;
	}

	public void setRandomNumBound(Integer randomNumBound) {
		this.randomNumBound = randomNumBound;
	}

}
