package com.ssafy.moiroomserver.matching.dto;

import java.util.List;

import com.ssafy.moiroomserver.member.dto.CharacteristicAndInterestInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MatchingInfo {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class MatchingResponse {
		private CharacteristicAndInterestInfo.RequestResponse memberOne;
		private List<CharacteristicAndInterestInfo.RequestResponse> memberTwos;

		@Builder
		public MatchingResponse(CharacteristicAndInterestInfo.RequestResponse memberOne,
				List<CharacteristicAndInterestInfo.RequestResponse> memberTwos) {
			this.memberOne = memberOne;
			this.memberTwos = memberTwos;
		}
	}

}